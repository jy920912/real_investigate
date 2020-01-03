<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.sql.*" %>
<%@ page import="javax.naming.*" %>
<%@ page import="javax.sql.*" %>
<%
/*
 * mysql 연결
 * JNDI 이용하여 연결
 */
Connection conn = null;
DataSource ds  = null;
PreparedStatement pstmt = null;
ResultSet rs = null;

final String DATASOUCE="jdbc/investigate";

Context ic = new InitialContext();
Context ec = (Context)ic.lookup("java:/comp/env");
ds = (DataSource)ec.lookup(DATASOUCE);
conn = ds.getConnection();

//직접 연결
/*Connection conn = null;
PreparedStatement pstmt = null;
ResultSet rs = null;
Class.forName("org.mariadb.jdbc.Driver");
String myUrl = "jdbc:mariadb://192.168.123.120:3307";
String sqlId = "realinvest";
String sqlPw = "it0088";
conn = DriverManager.getConnection(myUrl, sqlId, sqlPw);*/

%>
