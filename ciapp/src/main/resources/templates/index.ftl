<!DOCTYPE html>
<html>
<head>
    <title>CI App</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            border: 1px solid black;
            padding: 10px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
    <h1>CI Server is up and running</h1>
    <p>See previous builds below.</p>
    <table>
        <thead>
            <tr>
                <th>ID</th>
                <th>Commit Hash</th>
                <th>Build Date</th>
                <th>Logs</th>
            </tr>
        </thead>
        <tbody>
            <#list builds as build>
                <tr>
                    <td>${build.id?c}</td>
                    <td>${build.commitHash}</td>
                    <td>${build.buildDate}</td>
                    <td><a href="/build/${build.id?c}">View Log</a></td>
                </tr>
            </#list>
        </tbody>
    </table>
</body>
</html>
