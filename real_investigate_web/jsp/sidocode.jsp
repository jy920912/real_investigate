<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file ="mysql_conn.jsp" %>

<%
  /*
   * 실태조사 할 시도 불러오기
   */

  try{
    String s_Query = "select * from real_investigate_config.tb_ablesido";
    pstmt = conn.prepareStatement(s_Query);
    rs = pstmt.executeQuery();
    while(rs.next()){
      out.println(rs.getString("sido_cd")+","+rs.getString("sido_nm")+",");
    }
  }catch(Exception e){
    out.print("ERROR");
  }finally{
    if(rs != null) rs.close();
    if(pstmt != null) pstmt.close();
    if(conn != null) conn.close();
  }
%>
