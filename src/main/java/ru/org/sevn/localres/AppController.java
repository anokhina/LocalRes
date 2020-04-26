/*
 * Copyright 2020 Veronica Anokhina.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.org.sevn.localres;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.ServletException;

@Controller
public class AppController {
    private final File userDir = new File(System.getProperty("user.home"));
    private final File settings = new File(userDir, "ru.org.sevn/localres/settings");
    
    private String commandName = "explorer"; // "xdg-open";
    
    private final static String PARAM_EXPLORER = "explorer";
    
    @PostConstruct
    public void init() {
        try {
            if (settings.exists() && settings.canRead()) {
            final String str;
                str = new String(Files.readAllBytes(settings.toPath()), StandardCharsets.UTF_8);
                final JSONObject jo = new JSONObject(str);
                if (jo.has(PARAM_EXPLORER)) {
                    commandName = jo.getString(PARAM_EXPLORER);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @GetMapping(path = "/", produces = MediaType.TEXT_HTML_VALUE)
    public @ResponseBody String dir() throws Exception {
        final File rusName = new File(userDir, "Русское название");
        final File rusFile = new File(rusName, "Русский файл.txt");
        return "App is running " 
                + "<br><a href=\"" + rusFile.toURI() + "\">" + rusFile.toURI() + "</a> " + rusFile.toURI()
                + "<br><a href=\"" + rusName.toURI() + "\">" + rusName.toURI() + "</a> " + rusName.toURI()
                + "<br><a href=\"" + userDir.toURI() + "\">" + userDir.toURI() + "</a> " + userDir.toURI()
                + "<br><a href=\"" + rusName.getAbsolutePath() + "\">" + rusName.getAbsolutePath() + "</a> " + rusName.getAbsolutePath()
                + "<br><a href=\"" + rusFile.getAbsolutePath() + "\">" + rusFile.getAbsolutePath() + "</a> " + rusFile.getAbsolutePath()
                + "<br><a href=\"" + userDir.getAbsolutePath() + "\">" + userDir.getAbsolutePath() + "</a> " + userDir.getAbsolutePath()
                + "<br><a href=\"" + otherPath(rusName.getAbsolutePath()) + "\">" + otherPath(rusName.getAbsolutePath()) + "</a> " + otherPath(rusName.getAbsolutePath())
                + "<br><a href=\"" + otherPath(rusFile.getAbsolutePath()) + "\">" + otherPath(rusFile.getAbsolutePath()) + "</a> " + otherPath(rusFile.getAbsolutePath())
                + "<br><a href=\"" + otherPath(userDir.getAbsolutePath()) + "\">" + otherPath(userDir.getAbsolutePath()) + "</a> " + otherPath(userDir.getAbsolutePath())
                ;
    }
    
    //exo-open --launch FileManager . &
    @GetMapping(path = "/dir", produces = MediaType.TEXT_HTML_VALUE)
    public @ResponseBody String dir(@ModelAttribute LocalRes model) throws Exception {
        final String path = getFilePath(model.getPath());
        if (path != null) {
            final File dir = getDir(new File(path.trim()));
            final JSONObject jo = new JSONObject();
            jo.put("exists", dir.exists());
            jo.put("isDirectory", dir.isDirectory());
            jo.put("canRead", dir.canRead());
            if (dir.exists() && dir.canRead() && dir.isDirectory()) {
                ProcessBuilder builder = new ProcessBuilder();
                //builder.command("exo-open", "--launch", "FileManager");
                System.out.println(">>>>>>" + dir);
                builder.command(commandName, dir.getAbsolutePath());
                builder.directory(dir);
                builder.start();
                return "OK<script>window.close();</script>";
            } else {
                return "Can't open " + model.getPath() + " [" + path + "]" + jo.toString(2);
            }
        } else {
            return "Can't open " + model + " [" + path + "]";
        }
    }
    
    private File getDir(final File dir) {
        if (dir.exists() && !dir.isDirectory()) {
            final File res = dir.getParentFile();
            if (res != null) {
                return res;
            }
        }
        return dir;
    }

    private String getFilePath(final String url) {
        try {
            URI uri = new URI(url);
            return uri.getPath();
        } catch (URISyntaxException ex) {
            Logger.getLogger(AppController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (url != null) {
            if ('/' == File.separatorChar) {
                return url.replace('\\', File.separatorChar);
            } else {
                return url.replace('/', File.separatorChar);
            }
        }
                
        return url;
    }

    private String otherPath(String url) {
        if (url != null) {
            if ('/' == File.separatorChar) {
                return url.replace(File.separatorChar, '\\');
            } else {
                return url.replace(File.separatorChar, '/');
            }
        }
        
        return url;
    }
}
