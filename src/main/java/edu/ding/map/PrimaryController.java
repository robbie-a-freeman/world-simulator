package edu.ding.map;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

@RestController
@EnableAutoConfiguration
public class PrimaryController {

    @RequestMapping(value = "/tectonicMap", method = RequestMethod.GET)
    public void home(HttpServletResponse response) throws IOException {
        int[] worldSize = new int[] {500, 500};
        TectonicCellNetwork n = new TectonicCellNetwork(new BigDecimal(2), new BigDecimal(2));
        n.distributeCells(100);
        //System.out.println(n.getAllVertices().size());
        //System.out.println(n.getAllHalfEdges().size());
        //n.getAllHalfEdges().forEach((i, x) -> System.out.println(x.toString()));
        /*
        System.out.println(n.getNominalHalfEdges().get(0).getVertex());
        System.out.println(n.getNominalHalfEdges().get(0).getFaceId());
        System.out.println(n.getNominalHalfEdges().get(0).getNextEdge());
        System.out.println(n.getNominalHalfEdges().get(0).getId());
        System.out.println(n.getNominalHalfEdges().get(0).getOppositeEdge()); */

        // draw out the network
        JFrame f = new JFrame();
        TestPanel p = new TestPanel(n);
        f.add(p);
//        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(700, 700);
        f.setVisible(true);

        BufferedImage bi = new BufferedImage(f.getWidth(), f.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        f.print(g);
        f.dispose();
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        try {
            ImageIO.write(bi, "png", s);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ByteArrayInputStream in = new ByteArrayInputStream(s.toByteArray());
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        IOUtils.copy(in, response.getOutputStream());
    }



    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(PrimaryController.class);
        builder.headless(false).run(args);
    }

}
