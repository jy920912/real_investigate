<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file ="mysql_conn.jsp" %>

<%
  String s_pnu  = request.getParameter("PNU");
  String s_sido = request.getParameter("SIDO");
  try{
    String s_Query = "select * from "+s_sido+"_real_investigate.tb_pictureyesno where PNU = ?";
    pstmt = conn.prepareStatement(s_Query);
    pstmt.setString(1, s_pnu);
    rs = pstmt.executeQuery();
    while(rs.next()){
      out.print(rs.getString("COORDX")+"|"+rs.getString("COORDY")+"|");
    }
  }catch(Exception e){
    out.print("ERROR");
  }finally{
    if(rs != null) rs.close();
    if(pstmt != null) pstmt.close();
    if(conn != null) conn.close();
  }
%>
