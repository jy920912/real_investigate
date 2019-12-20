<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file ="mysql_conn.jsp" %>

<%
  String s_address = request.getParameter("NAME");
  String s_sido    = request.getParameter("SIDO");
  try{
    String s_Query = "select COORD_X, COORD_Y from real_investigate_"+s_sido+".tb_coordxy where NOW_ADDR like ? ";
    pstmt = conn.prepareStatement(s_Query);
    pstmt.setString(1, "%"+s_address);
    rs = pstmt.executeQuery();
    while(rs.next()){
      out.print(rs.getString("COORD_X")+","+rs.getString("COORD_Y")+",");
    }
  }catch(Exception e){
    out.print(e);
  }
  if(rs != null) rs.close();
  if(pstmt != null) pstmt.close();
  if(conn != null) conn.close();
%>
