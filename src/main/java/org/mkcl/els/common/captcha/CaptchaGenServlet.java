package org.mkcl.els.common.captcha;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.servlet.http.*;
import java.io.*;

public class CaptchaGenServlet extends HttpServlet {
	public static final String FILE_TYPE = "jpeg";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Max-Age", 0);
		String referrer = request.getHeader("referer");
		// System.out.println("referrer"+referrer);
		if (referrer == null) {
			// response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not allowed.");
		} else {
			String captchaStr = "";

			boolean type = false;//(Math.random() < 0.5);
			if (type) {
				captchaStr = CaptchaUtil.generateCaptchaTextMethod2(6);
				HttpSession session = request.getSession(true);
				session.setAttribute("CAPTCHA", captchaStr);

			} else {
				captchaStr = CaptchaUtil.generateCaptchaExpression();
				String comp[] = captchaStr.split("\\+");
				int sum = Integer.parseInt(comp[0]) + Integer.parseInt(comp[1]);
				HttpSession session = request.getSession(true);
				session.setAttribute("CAPTCHA", new Integer(sum).toString());
				captchaStr = captchaStr + "= ?";
			}
			try {

				int width = 150;
				int height = 40;

				Color bg = new Color(255, 245, 157);

				Color fg = new Color(26, 35, 126);

				Font font = new Font("Arial", Font.BOLD, 20);
				BufferedImage cpimg = new BufferedImage(width, height, BufferedImage.OPAQUE);
				Graphics g = cpimg.createGraphics();

				g.setFont(font);
				g.setColor(bg);
				g.fillRect(0, 0, width, height);
				g.setColor(fg);

				FontMetrics fm = g.getFontMetrics();
				int x = ((width - fm.stringWidth(captchaStr)) / 2);
				int y = ((height - fm.getHeight()) / 2) + fm.getAscent();

				g.drawString(captchaStr, x, y);

				OutputStream outputStream = response.getOutputStream();

				ImageIO.write(cpimg, FILE_TYPE, outputStream);
				outputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

}
