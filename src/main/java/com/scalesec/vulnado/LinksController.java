package com.scalesec.vulnado;

import com.scalesec.vulnado.CustomBadRequestException;
import com.scalesec.vulnado.LinkLister;
import org.springframework.boot.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.boot.autoconfigure.*;
import java.util.List;
import java.io.Serializable;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@RestController
@EnableAutoConfiguration
public class LinksController {
  private static final Set<String> ALLOWED_DOMAINS = new HashSet<>(Arrays.asList("example.com", "trusted-domain.com"));
  private static final Pattern URL_PATTERN = Pattern.compile("^https?://[a-zA-Z0-9.-]+(\\.[a-zA-Z]{2,})?(:\\d{1,5})?(/.*)?$");

  @RequestMapping(value = "/links", produces = "application/json")
  List<String> links(@RequestParam String url) throws IOException {
    if (isValidUrl(url)) {
      return LinkLister.getLinks(url);
    } else {
      throw new CustomBadRequestException("Invalid URL");
    }
  }

  @RequestMapping(value = "/links-v2", produces = "application/json")
  List<String> linksV2(@RequestParam String url) throws CustomBadRequestException, IOException {
    if (isValidUrl(url)) {
      return LinkLister.getLinksV2(url);
    } else {
      throw new CustomBadRequestException("Invalid URL");
    }
  }

  private boolean isValidUrl(String url) {
    if (URL_PATTERN.matcher(url).matches()) {
      try {
        URL inputUrl = new URL(url);
        String host = inputUrl.getHost();
        return ALLOWED_DOMAINS.contains(host);
      } catch (Exception e) {
        return false;
      }
    }
    return false;
  }
}
