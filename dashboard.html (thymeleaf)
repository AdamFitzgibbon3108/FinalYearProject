<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ThreatLens Dashboard</title>
    <style>
        body, html {
            margin: 0;
            padding: 0;
            height: 100vh;
            font-family: Arial, sans-serif;
            background: radial-gradient(circle at center, #141e30, #243b55), url('/images/cybersecurity-grid.png');
            background-size: cover, contain;
            background-blend-mode: overlay;
            color: #ffffff;
        }

        .header {
            background: rgba(20, 30, 48, 0.95);
            padding: 1.5rem;
            text-align: center;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.3);
            border-bottom: 2px solid #58a6ff;
        }

        .header h1 {
            margin: 0;
            font-size: 2.8rem;
            color: #58a6ff;
        }

        .header p {
            margin-top: 0.5rem;
            font-size: 1.2rem;
            color: #cbd5e0;
        }

        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 2rem;
            background: rgba(20, 30, 48, 0.95);
            border-radius: 15px;
            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.5);
        }

        .welcome {
            text-align: center;
            margin-bottom: 2rem;
        }

        .welcome p {
            font-size: 1.4rem;
            color: #cbd5e0;
        }

        .actions {
            display: flex;
            flex-wrap: wrap;
            justify-content: space-around;
            gap: 2rem;
        }

        .action-card {
            background: rgba(40, 50, 70, 0.9);
            width: 280px;
            padding: 1.5rem;
            border-radius: 15px;
            text-align: center;
            box-shadow: 0 6px 15px rgba(0, 0, 0, 0.5);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }

        .action-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
        }

        .action-card h3 {
            margin: 0;
            font-size: 1.5rem;
            color: #58a6ff;
        }

        .action-card p {
            margin: 1rem 0;
            font-size: 1rem;
            color: #cbd5e0;
        }

        .action-card a {
            text-decoration: none;
        }

        .action-card button {
            padding: 0.8rem 1.2rem;
            background: linear-gradient(135deg, #58a6ff, #1e90ff);
            border: none;
            border-radius: 8px;
            color: #ffffff;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: background 0.3s ease, transform 0.2s ease;
        }

        .action-card button:hover {
            background: linear-gradient(135deg, #1e90ff, #58a6ff);
            transform: translateY(-2px);
            box-shadow: 0 6px 12px rgba(88, 166, 255, 0.4);
        }

        .logout {
            text-align: center;
            margin-top: 3rem;
        }

        .logout a {
            color: #58a6ff;
            font-size: 1.1rem;
            text-decoration: none;
        }

        .logout a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>ThreatLens Dashboard</h1>
        <p>Stay updated with real-time security insights and take control of your cybersecurity journey.</p>
    </div>

    <div class="container">
        <!-- Welcome Section -->
        <div class="welcome">
            <p>Welcome back, <span th:text="${username}"></span>!</p>
            <p>Select an action to get started:</p>
        </div>

        <!-- Actions Section -->
        <div class="actions">
            <!-- Security Questionnaire -->
            <div class="action-card">
                <h3>Security Questionnaire</h3>
                <p>Evaluate your risk areas and strengthen your position.</p>
                <a th:href="@{/dashboard/questionnaire}">
                    <button>Start Questionnaire</button>
                </a>
            </div>

            <!-- Profile Section -->
            <div class="action-card">
                <h3>View Profile</h3>
                <p>Manage your account and view recent activity.</p>
                <a th:href="@{/profile}">
                    <button>Go to Profile</button>
                </a>
            </div>

            <!-- Graphs/Reports -->
            <div class="action-card">
                <h3>Real-Time Insights</h3>
                <p>Visualize your risk score trends and security metrics.</p>
                <a th:href="@{/dashboard/reports}">
                    <button>View Reports</button>
                </a>
            </div>

            <!-- Gamification -->
            <div class="action-card">
                <h3>Challenges & Rewards</h3>
                <p>Complete challenges, earn badges, and climb the leaderboard.</p>
                <a th:href="@{/dashboard/challenges}">
                    <button>Start Challenges</button>
                </a>
            </div>

            <!-- Training Programs -->
            <div class="action-card">
                <h3>Training Programs</h3>
                <p>Access personalized security training to bridge skill gaps.</p>
                <a th:href="@{/training}">
                    <button>View Trainings</button>
                </a>
            </div>

            <!-- Real-Time Alerts -->
            <div class="action-card">
                <h3>Real-Time Alerts</h3>
                <p>Stay informed with the latest cybersecurity threats and updates.</p>
                <a th:href="@{/alerts}">
                    <button>View Alerts</button>
                </a>
            </div>
        </div>

        <!-- Logout Section -->
        <div class="logout">
            <p><a href="/logout">Logout</a></p>
        </div>
    </div>
</body>
</html>
