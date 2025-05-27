package ru.itmo.credithistory.reportservice.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;
import ru.itmo.credithistory.reportservice.dto.ReportDataDto;

@Service
@RequiredArgsConstructor
@NullMarked
public class GenerationService {

  private final Configuration configuration;

  public String generateCreditReport(ReportDataDto reportData) throws Exception {
    Template template = configuration.getTemplate("credit_report.md.fthl");

    Map<String, Object> model = new HashMap<>();
    model.put("report", reportData);

    try (StringWriter out = new StringWriter()) {
      template.process(model, out);
      return out.toString();
    }
  }
}
