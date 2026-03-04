<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Fehler - SAP Consulting App</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .error-box { border: 1px solid #d9534f; background: #fdf2f2; padding: 20px; border-radius: 4px; }
        h1 { color: #d9534f; }
        a { color: #337ab7; }
    </style>
</head>
<body>
    <div class="error-box">
        <h1>Ein Fehler ist aufgetreten</h1>
        <p><strong>Fehlercode:</strong> <%= request.getAttribute("javax.servlet.error.status_code") %></p>
        <p><strong>Meldung:</strong> <%= request.getAttribute("javax.servlet.error.message") != null
                ? request.getAttribute("javax.servlet.error.message") : "Unbekannter Fehler" %></p>
        <p><a href="<%= request.getContextPath() %>/dashboard">&#8592; Zurueck zum Dashboard</a></p>
    </div>
</body>
</html>
