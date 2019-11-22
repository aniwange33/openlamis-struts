/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhi360.lamis.utility;

import com.github.jhonnymertz.wkhtmltopdf.wrapper.configurations.WrapperConfig;

public class WKHtmlWrapperConfig extends WrapperConfig {
    public String findExecutable() {
        return "C:/LAMIS3/wkhtmltopdf";
    }
}
