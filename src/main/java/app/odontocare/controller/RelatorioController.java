package app.odontocare.controller;

import app.odontocare.service.RelatorioService;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping("/relatorios") // Todas as URLs de relatório começarão com /relatorios
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    // MÉTODO NOVO: Exibe a página principal de relatórios
    @GetMapping
    public String mostrarPaginaRelatorios() {
        return "relatorio/pagina-relatorios";
    }

    // MÉTODO EXISTENTE: Gera o relatório de consultas
    @GetMapping("/consultas")
    public void gerarRelatorioConsultas(HttpServletResponse response) throws JRException, IOException {
        byte[] pdfBytes = relatorioService.gerarRelatorio("relatorio_mestre_consultas");

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-Disposition", "inline; filename=relatorio_de_consultas.pdf");
        response.getOutputStream().write(pdfBytes);
    }

    // MÉTODO NOVO: Gera o relatório de clientes (exemplo)
    @GetMapping("/clientes")
    public void gerarRelatorioClientes(HttpServletResponse response) throws JRException, IOException {
        // O mesmo service pode ser reutilizado para gerar qualquer relatório
        // que busca seus próprios dados via SQL.
        byte[] pdfBytes = relatorioService.gerarRelatorio("clientes"); // Assumindo que você criou um relatório com este nome

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-Disposition", "inline; filename=relatorio_de_clientes.pdf");
        response.getOutputStream().write(pdfBytes);
    }
}