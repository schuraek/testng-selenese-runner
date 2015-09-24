package de.kosch.testcase.webresources;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jboss.netty.util.CharsetUtil;
import org.openqa.selenium.net.PortProber;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServers;
import org.webbitserver.handler.StaticFileHandler;
import org.webbitserver.handler.authentication.BasicAuthenticationHandler;
import org.webbitserver.handler.authentication.InMemoryPasswords;

/**
 * Webserver for unit test.
 */
public class WebServer {

    private static final int STOP_RETRY_COUNT = 3;

    private int port;

    private String fqdn = "localhost";

    private org.webbitserver.WebServer server;

    private static class FormPosted implements HttpHandler {
        @Override
        public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control) throws Exception {
            String uri = request.uri();
            String path = uri.replaceAll("\\?.*$", "");
            InputStream is = getClass().getResourceAsStream("/htdocs" + path);
            String html = IOUtils.toString(is, CharsetUtil.UTF_8)
                .replaceFirst("(?s)<script[^>]*>.*?</script>", "") // remove script tag.
                .replaceFirst("<body.*?>", "<body>"); // remove "onload" handler.
            List<String> keys = new ArrayList<String>(request.queryParamKeys());
            Collections.sort(keys);
            for (String key : keys) {
                String value = request.queryParam(key);
                html = html.replaceFirst("(<span\\s+id=\"" + key + "\">)(</span>)", "$1" + StringEscapeUtils.escapeHtml4(value) + "$2");
            }
            response.charset(CharsetUtil.UTF_8).content(html).end();
        }
    }

    /**
     * Start web server.
     */
    public void start() {
        port = PortProber.findFreePort();
        File htdocs = FileUtils.toFile(getClass().getResource("/htdocs"));
        server = WebServers.createWebServer(port)
            .add("/form_posted\\.html", new FormPosted())
            .add(new StaticFileHandler(htdocs))
            .add("/basic", new BasicAuthenticationHandler(new InMemoryPasswords().add("user", "pass")))
            .add("/redirect", new RedirectHandler("http://" + getServerNameString() + "/index.html"))
            .add("/basic/redirect", new RedirectHandler("http://" + getServerNameString() + "/basic/index.html"));
        try {
            server.start().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stop web server.
     */
    public void stop() {
        try {
            int retry = STOP_RETRY_COUNT;
            while (true) {
                try {
                    server.stop().get();
                    break;
                } catch (AssertionError e) {
                    // WebServer.stop() throw AssertionError frequently, and retry stopping.
                    if (--retry > 0)
                        throw e;
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get server name.
     *
     * @return server name.
     */
    public String getServerNameString() {
        return fqdn + ":" + port;
    }

    /**
     * Get base URL.
     *
     * @return base URL.
     */
    public String getBaseURL() {
        return "http://" + getServerNameString() + "/";
    }

    /**
     * Set FQDN of base URL for android test.
     *
     * @param fqdn FQDN.
     */
    public void setFqdn(String fqdn) {
        this.fqdn = fqdn;
    }
}
