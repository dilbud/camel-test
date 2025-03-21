package org.dbx;

import org.apache.camel.CamelContext;
import org.apache.camel.component.xslt.XsltUriResolver;
import org.apache.camel.support.ResourceHelper;
import org.apache.camel.util.FileUtil;
import org.apache.camel.util.ObjectHelper;
import org.apache.camel.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class CustomXsltUriResolver<T>
        implements URIResolver
{
    private static final Logger LOG = LoggerFactory.getLogger(CustomXsltUriResolver.class);
    private final Class<T> classType;

    public CustomXsltUriResolver(Class<T> classType) {
        this.classType = classType;
    }

    public Source resolve(String href, String base) throws TransformerException {

        String baseScheme = null;

        if (ResourceHelper.hasScheme(href)) {
            baseScheme = ResourceHelper.getScheme(href);
        } else {
            baseScheme = "classpath:";
        }

        if (ObjectHelper.isEmpty(href)) {
            throw new TransformerException("include href is empty");
        } else {
            LOG.trace("Resolving URI with href: {} and base: {}", href, base);
            String scheme = ResourceHelper.getScheme(href);
            if (scheme != null) {
                String hrefPath = StringHelper.after(href, scheme);
                if ("file:".equals(scheme)) {
                    href = scheme + FileUtil.compactPath(hrefPath);
                } else if ("classpath:".equals(scheme)) {
                    href = scheme + FileUtil.compactPath(hrefPath, '/');
                }

                LOG.debug("Resolving URI from {}: {}", scheme, href);
                var ss = href.replace(baseScheme, "");
                InputStream is = classType.getClassLoader().getResourceAsStream(ss);
                if(Objects.isNull(is)) {
                    throw new TransformerException("Could not find resource: " + href);
                }

                return new StreamSource(is, href);
            }else {
                String path = FileUtil.onlyPath(base);
                if (ObjectHelper.isEmpty(path)) {
                    path = baseScheme + href;
                    return this.resolve(path, base);
                } else {
                    if (ResourceHelper.hasScheme(path)) {
                        path = path + "/" + href;
                    } else {
                        path = baseScheme + path + "/" + href;
                    }

                    return this.resolve(path, base);
                }
            }
        }
    }
}
