<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file ="mysql_conn.jsp" %>

<%
  /*
   * 해당 시군에 있는 대상지 전체 select
   */
  String s_sido = request.getParameter("SIDO");

  try{
    String s_Query = "select * from real_investigate_"+s_sido+".tb_pictureyesno";
    pstmt = conn.prepareStatement(s_Query);
    rs = pstmt.executeQuery();
    while(rs.next()){
      out.print(rs.getString("PNU")+"|"+rs.getString("PICTURE_OX")+"|"+rs.getString("SEND_OX")+"|"
                +rs.getString("DRON_OX")+"|"+rs.getString("COORDX")+"|"+rs.getString("COORDY")+"|");
    }
  }catch(Exception e){
    out.print("ERROR");
  }finally{
    if(rs != null) rs.close();
    if(pstmt != null) pstmt.close();
    if(conn != null) conn.close();
  }
%>
