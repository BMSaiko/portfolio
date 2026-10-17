package com.example.sheetsandpicks;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;
import javax.xml.XMLConstants;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;


import java.io.File;
import java.io.IOException;

public class ConfirmaXMLController {

    @FXML
    private Label mensagem;

    @FXML
    public void xmlverification(ActionEvent event) {
        FileChooser ficheiroxml = new FileChooser();
        ficheiroxml.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
        File ficheiroselecionado = ficheiroxml.showOpenDialog(null);

        if(ficheiroselecionado != null){
            try {
                SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                System.out.println(new File("XSDverificator.xsd").getAbsolutePath());
                Schema esquema = factory.newSchema(new StreamSource(new File("C:\\Trabalho\\Fichalp3\\gp2-lp3\\XSDverificator.xsd")));
                Validator validar = esquema.newValidator();
                validar.validate(new StreamSource(ficheiroselecionado));

                exibirPopUpSucesso("XML válido.");
            } catch (SAXException | IOException e) {
                exibirPopUpErro("Erro na validação do XML " + e.getMessage());
            }
        } else {
            exibirPopUpErro("Nenhum arquivo selecionado");
        }
    }

    private void exibirPopUpSucesso(String mensagem) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void exibirPopUpErro(String mensagem) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}