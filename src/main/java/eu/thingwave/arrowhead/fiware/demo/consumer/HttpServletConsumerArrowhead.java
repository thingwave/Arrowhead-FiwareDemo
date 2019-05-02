/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.thingwave.arrowhead.fiware.demo.consumer;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@thingwave.eu>
 */
public class HttpServletConsumerArrowhead extends HttpServlet {
 
    @Override
    protected void doGet(
      HttpServletRequest request, 
      HttpServletResponse response)
      throws ServletException, IOException {
  
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("{\"info\":\"Hi, I'm a demo client for incomming messages from Arrowhead\"}");
    }
}