<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file ="mysql_conn.jsp" %>

<%
  String id = request.getParameter("id");
  String pw = request.getParameter("pw");

  try{
    String s_Query = "select count(id) ok from real_investigate_config.tb_user_info where id = ? and pw = ?";
    pstmt = conn.prepareStatement(s_Query);
    pstmt.setString(1, id);
    pstmt.setString(2, pw);
    rs = pstmt.executeQuery();
    while(rs.next()){
      out.println(rs.getString("ok"));
    }
  }catch(Exception e){
    out.print(e);
  }finally{
    if(rs != null) rs.close();
    if(pstmt != null) pstmt.close();
    if(conn != null) conn.close();
  }
%>
