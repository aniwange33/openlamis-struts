package org.fhi360.lamis.utility;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import org.apache.commons.io.IOUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.expression.IStandardVariableExpression;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.OGNLVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.StandardExpressionExecutionContext;

public class PDFUtil {
    
    public static byte[] generate(String template, Map<String, Object> parameters, Object dataSource, boolean landscape) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(3600000L);
        templateResolver.setCacheable(true);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.clearDialects();
        templateEngine.addDialect(getDialect());
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context();
        context.setVariables(parameters);
        context.setVariable("dataSource", dataSource);
        
        InputStream is = PDFUtil.class.getClassLoader().getResourceAsStream("style.css");
        String tmpDir = System.getProperty("java.io.tmpdir");
        try {
            if (is != null) {
                IOUtils.copy(is, new FileOutputStream(new File(tmpDir + "/style.css")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        String content = templateEngine.process(template, context);
        WrapperConfig wrapperConfig = new WKHtmlWrapperConfig();
        Pdf pdf = new Pdf(wrapperConfig);
        
        pdf.addPageFromString(content);
        if (landscape) {
            pdf.addParam(new Param("-O", "Landscape"));
        }
        byte[] data = null;
        try {
            data = pdf.getPDF();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    static {
        try {
            String hash = "db9c71d08593b3c937851089c9bbf974";
            String md5Hex = null;
            boolean exists = new File("c:/lamis3/wkhtmltopdf.exe").exists();
            if (exists) {
                md5Hex = DigestUtils.md5Hex(new FileInputStream("c:/lamis3/wkhtmltopdf.exe")).toUpperCase();
            }
            if (!exists || (exists && !hash.equalsIgnoreCase(md5Hex))) {
                SevenZ.decompress(PDFUtil.class.getClassLoader()
                        .getResource("wkhtmltopdf.7z").getPath(), new File("c:/lamis3/"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static IDialect getDialect() {
        StandardDialect dialect = new StandardDialect();
        try {
            Class.forName("com.opensymphony.xwork2.ognl.accessor.ObjectAccessor"); //check has struts
        } catch (ClassNotFoundException e) {
            return dialect;
        }
        try {//replace the singleton map with hashmap
            Field field = OGNLVariableExpressionEvaluator.class.getDeclaredField("CONTEXT_VARIABLES_MAP_NOEXPOBJECTS_RESTRICTIONS");
            field.setAccessible(true);
            field.set(null, new HashMap<>());
        } catch (Exception e) {
            throw new RuntimeException("Thymleaf can't work with struts.", e);
        }
        OGNLVariableExpressionEvaluator evaluator = new OGNLVariableExpressionEvaluator(true);
        dialect.setVariableExpressionEvaluator(new IStandardVariableExpressionEvaluator() {
            @Override
            public Object evaluate(IExpressionContext context,
                    IStandardVariableExpression expression,
                    StandardExpressionExecutionContext expContext) {
                expContext = StandardExpressionExecutionContext.RESTRICTED; //false, false -> true, false
                return evaluator.evaluate(context, expression, expContext);
            }
        });
        return dialect;
    }
}
