<jsp:useBean id="managedashboardDashboard" scope="session" class="fr.paris.lutece.plugins.publicdashboard.web.PublicDashboardJspBean" />
<% String strContent = managedashboardDashboard.processController ( request , response ); %>

<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../AdminHeader.jsp" />

<%= strContent %>

<%@ include file="../../AdminFooter.jsp" %>
