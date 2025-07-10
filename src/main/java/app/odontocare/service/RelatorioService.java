package app.odontocare.service;

import net.sf.jasperreports.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
public class RelatorioService {

    @Autowired
    private DataSource dataSource; // Injete o DataSource para obter a conexão com o BD

    public byte[] gerarRelatorio(String nomeRelatorio) throws JRException {
        // Carrega o arquivo .jasper do classpath (da pasta /resources/reports)
        InputStream relatorioStream = getClass().getResourceAsStream("/reports/" + nomeRelatorio + ".jasper");

        Connection connection = null;
        try {
            // Obtém uma conexão com o banco de dados
            connection = dataSource.getConnection();

            // Parâmetros para o relatório
            Map<String, Object> parametros = new HashMap<>();
            
            // ADICIONADO: Passa o caminho do diretório de sub-relatórios para o relatório principal.
            // O valor deve ser o nome da pasta dentro de 'resources' com uma barra no final.
            parametros.put("SUBREPORT_DIR", "reports/");

            // Preenche o relatório, passando a conexão para que ele possa executar sua própria query SQL
            JasperPrint jasperPrint = JasperFillManager.fillReport(relatorioStream, parametros, connection);

            // Exporta o relatório preenchido para PDF, retornando um array de bytes
            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (SQLException e) {
            // Trata a exceção de SQL
            throw new RuntimeException("Não foi possível obter a conexão com o banco de dados.", e);
        } finally {
            // Garante que a conexão seja fechada
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // Apenas loga o erro, não lança uma nova exceção aqui
                    System.err.println("Não foi possível fechar a conexão com o banco de dados: " + e.getMessage());
                }
            }
        }
    }
}
