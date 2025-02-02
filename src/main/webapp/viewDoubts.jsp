<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.app.Doubt" %>

<html>
<head>
    <title>View Doubts</title>
</head>
<body>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>User ID</th>
            <th>Department</th>
            <th>Doubt Text</th>
            <th>Audio Path</th>
        </tr>
        <% 
            Object doubtsObj = request.getAttribute("doubts");
            if (doubtsObj instanceof List<?>) {
                List<?> rawList = (List<?>) doubtsObj;
                for (Object obj : rawList) {
                    if (obj instanceof Doubt) {
                        Doubt doubt = (Doubt) obj;
        %>
        <tr>
            <td><%= doubt.getDoubtId() %></td>
            <td><%= doubt.getUserId() %></td>
            <td><%= doubt.getDepartment() %></td>
            <td><%= doubt.getDoubtText() %></td>
            <td><%= doubt.getAudioPath() %></td>
        </tr>
        <% 
                    }
                }
            } else {
        %>
        <tr>
            <td colspan="5">No doubts found.</td>
        </tr>
        <% 
            }
        %>
    </table>
</body>
</html>
