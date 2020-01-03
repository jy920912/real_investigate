<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file ="mysql_conn.jsp" %>

<%
  /*
   * id와 pw 일치 여부 확인
   * 안드로이드에서 받은 id, pw에 따른 개수 확인
   */

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
    out.print("ERROR");
  }finally{
    if(rs != null) rs.close();
    if(pstmt != null) pstmt.close();
    if(conn != null) conn.close();
  }
%>
