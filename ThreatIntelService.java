package com.example.service;

import com.example.model.CveItem;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThreatIntelService {

    private static final Logger logger = LoggerFactory.getLogger(ThreatIntelService.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<CveItem> fetchLatestCves() {
        logger.info("==> Entered fetchLatestCves()");
        List<CveItem> cveItems = new ArrayList<>();
        String url = "https://cve.circl.lu/api/last";

        try {
            logger.info("Sending GET request to {}", url);
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);

            if (root.isArray()) {
                for (JsonNode node : root) {
                    boolean parsed = false;

                    String id = "n/a";
                    String summary = "n/a";
                    String published = "n/a";
                    String modifiedDate = "n/a";
                    String baseScore = "n/a";
                    String cwe = "n/a";
                    String advisoryUrl = "n/a";
                    String displayId = "n/a";
                    String sourceLink = "n/a";

                    // Format 1: Red Hat CSAF
                    if (node.has("document")) {
                        parsed = true;
                        JsonNode doc = node.path("document");
                        String rhsaId = doc.path("tracking").path("id").asText("n/a");
                        String extractedCve = extractCveFromText(doc.path("notes"));
                        summary = extractNoteValue(doc.path("notes"), "summary");

                        published = doc.path("tracking").path("initial_release_date").asText("n/a");
                        modifiedDate = doc.path("tracking").path("current_release_date").asText("n/a");
                        cwe = "Red Hat Advisory";

                        JsonNode references = doc.path("references");
                        for (JsonNode ref : references) {
                            if ("self".equals(ref.path("category").asText()) && ref.has("url")) {
                                advisoryUrl = ref.path("url").asText("n/a");
                                break;
                            }
                        }

                        id = extractedCve.equals("n/a") ? rhsaId : extractedCve;
                        displayId = rhsaId;
                        sourceLink = advisoryUrl;

                        if (!"n/a".equals(rhsaId) && !"n/a".equals(summary)) {
                            cveItems.add(new CveItem(id, summary, published, modifiedDate, baseScore, cwe, advisoryUrl, displayId, sourceLink));
                        }
                    }

                    // Format 2: Legacy CVE (must not be GHSA)
                    else if (node.has("id")) {
                        id = node.path("id").asText("n/a");

                        if (id.startsWith("GHSA-")) {
                            logger.info("⛔ Skipping GHSA entry: {}", id);
                            continue;
                        }

                        parsed = true;
                        displayId = id;
                        published = node.path("published").asText("n/a");
                        modifiedDate = node.path("lastModified").asText("n/a");
                        sourceLink = "https://nvd.nist.gov/vuln/detail/" + id;
                        advisoryUrl = sourceLink;

                        JsonNode descriptions = node.path("descriptions");
                        if (descriptions.isArray()) {
                            for (JsonNode desc : descriptions) {
                                if ("en".equals(desc.path("lang").asText())) {
                                    summary = desc.path("value").asText("n/a");
                                    break;
                                }
                            }
                        }

                        JsonNode metrics = node.path("metrics").path("cvssMetricV31");
                        if (metrics.isArray() && metrics.size() > 0) {
                            JsonNode cvssData = metrics.get(0).path("cvssData");
                            baseScore = cvssData.path("baseScore").asText("n/a");
                        }

                        JsonNode weaknesses = node.path("weaknesses");
                        if (weaknesses.isArray() && weaknesses.size() > 0) {
                            JsonNode descNode = weaknesses.get(0).path("description");
                            if (descNode.isArray() && descNode.size() > 0) {
                                cwe = descNode.get(0).path("value").asText("n/a");
                            }
                        }

                        if (!"n/a".equals(id)) {
                            cveItems.add(new CveItem(id, summary, published, modifiedDate, baseScore, cwe, advisoryUrl, displayId, sourceLink));
                        }
                    }

                    // Format 3: CVE 5.1 Modern Format
                    else if (node.has("cveMetadata") && node.has("containers")) {
                        parsed = true;
                        JsonNode metadata = node.path("cveMetadata");
                        JsonNode cna = node.path("containers").path("cna");

                        id = metadata.path("cveId").asText("n/a");
                        displayId = id;
                        published = metadata.path("datePublished").asText("n/a");
                        modifiedDate = metadata.path("dateUpdated").asText("n/a");

                        JsonNode descriptions = cna.path("descriptions");
                        if (descriptions.isArray()) {
                            for (JsonNode desc : descriptions) {
                                if ("en".equals(desc.path("lang").asText())) {
                                    summary = desc.path("value").asText("n/a");
                                    break;
                                }
                            }
                        }

                        JsonNode refs = cna.path("references");
                        if (refs.isArray() && refs.size() > 0) {
                            advisoryUrl = refs.get(0).path("url").asText("n/a");
                        }

                        sourceLink = advisoryUrl;

                        // CNA CVSS
                        JsonNode metrics = cna.path("metrics");
                        if (metrics.isArray()) {
                            for (JsonNode metric : metrics) {
                                JsonNode cvss = metric.path("cvssV3_1");
                                if (!cvss.isMissingNode()) {
                                    baseScore = cvss.path("baseScore").asText("n/a");
                                    break;
                                }
                            }
                        }

                        // ADP fallback CVSS
                        if ("n/a".equals(baseScore)) {
                            JsonNode adp = node.path("containers").path("adp");
                            if (adp.isArray()) {
                                for (JsonNode adpEntry : adp) {
                                    JsonNode adpMetrics = adpEntry.path("metrics");
                                    if (adpMetrics.isArray()) {
                                        for (JsonNode metric : adpMetrics) {
                                            JsonNode cvss = metric.path("cvssV3_1");
                                            if (!cvss.isMissingNode()) {
                                                baseScore = cvss.path("baseScore").asText("n/a");
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (!"n/a".equals(id)) {
                            cveItems.add(new CveItem(id, summary, published, modifiedDate, baseScore, cwe, advisoryUrl, displayId, sourceLink));
                        }
                    }

                    if (!parsed) {
                        logger.warn("⚠️ Unsupported CVE entry skipped: {}", node.toPrettyString().substring(0, Math.min(node.toPrettyString().length(), 300)));
                    }
                }
            } else {
                logger.warn("❌ Unexpected JSON root structure");
            }

        } catch (Exception e) {
            logger.error("❌ Exception during CVE fetch: {}", e.getMessage(), e);
        }

        logger.info("<== Exiting fetchLatestCves() with {} entries", cveItems.size());
        return cveItems;
    }

    private String extractNoteValue(JsonNode notes, String category) {
        if (notes != null && notes.isArray()) {
            for (JsonNode note : notes) {
                if (category.equalsIgnoreCase(note.path("category").asText())) {
                    return note.path("text").asText("n/a");
                }
            }
        }
        return "n/a";
    }

    private String extractCveFromText(JsonNode notes) {
        if (notes != null && notes.isArray()) {
            for (JsonNode note : notes) {
                String text = note.path("text").asText();
                if (text.contains("CVE-")) {
                    int start = text.indexOf("CVE-");
                    int end = text.indexOf(" ", start);
                    return text.substring(start, end > start ? end : text.length()).trim();
                }
            }
        }
        return "n/a";
    }
}

