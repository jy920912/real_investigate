<%@ page contentType="text/html; charset=utf-8" %>
<%@ include file ="mysql_conn.jsp" %>

<%
  String s_pnu    = request.getParameter("PNU");
  String s_capOnOff = request.getParameter("CAPONFF");
  String s_sndOnOff = request.getParameter("SNDONOFF");
  String s_drnOnOff = request.getParameter("DRNONOFF");
  String s_uid    = request.getParameter("UID");
  String s_sido = request.getParameter("SIDO");
  double d_coordx = Double.parseDouble(request.getParameter("X"));
  double d_coordy = Double.parseDouble(request.getParameter("Y"));
  try{
    if("U".equals(s_uid)){
      String s_Query = "update "+s_sido+"_real_investigate.tb_pictureyesno set PICTURE_OX = ? "+
                       ",SEND_OX = ? ,DRON_OX = ? where PNU = ? ";
      pstmt = conn.prepareStatement(s_Query);
      pstmt.setString(1, s_capOnOff);
      pstmt.setString(2, s_sndOnOff);
      pstmt.setString(3, s_drnOnOff);
      pstmt.setString(4, s_pnu);
      int result = pstmt.executeUpdate();
      out.print(result);
    }
    else if("I".equals(s_uid)){
      String s_Query = "insert into "+s_sido+"_real_investigate.tb_pictureyesno values(?,?,?,?,?,?)";
      pstmt = conn.prepareStatement(s_Query);
      pstmt.setString(1, s_pnu);
      pstmt.setString(2, s_capOnOff);
      pstmt.setString(3, s_sndOnOff);
      pstmt.setString(4, s_drnOnOff);
      pstmt.setDouble(5, d_coordx);
      pstmt.setDouble(6, d_coordy);
      int result = pstmt.executeUpdate();
      out.print(result);
    }
    else if("D".equals(s_uid)){
      String s_Query = "delete from "+s_sido+"_real_investigate.tb_pictureyesno where PNU = ?";
      pstmt = conn.prepareStatement(s_Query);
      pstmt.setString(1, s_pnu);
      int result = pstmt.executeUpdate();
      out.print(result);
    }
  }catch(Exception e){
    out.print(e);
  }finally{
    if(pstmt != null) pstmt.close();
    if(conn != null) conn.close();
  }
%>
