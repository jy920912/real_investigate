<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file ="mysql_conn.jsp" %>

<%
  String s_sido = request.getParameter("SIDO");

  try{
    String s_Query = "select * from "+s_sido+"_real_investigate.tb_pictureyesno";
    pstmt = conn.prepareStatement(s_Query);
    rs = pstmt.executeQuery();
    while(rs.next()){
      out.print(rs.getString("PNU")+"|"+rs.getString("PICTURE_OX")+"|"+rs.getString("SEND_OX")+"|"
                +rs.getString("DRON_OX")+"|"+rs.getString("COORDX")+"|"+rs.getString("COORDY")+"|");
    }
  }catch(Exception e){
    out.print(e);
  }finally{
    if(rs != null) rs.close();
    if(pstmt != null) pstmt.close();
    if(conn != null) conn.close();
  }
%>
