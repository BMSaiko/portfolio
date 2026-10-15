package org.companhiaaerea.utils;

import org.companhiaaerea.*;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.companhiaaerea.Main.voos;
import static org.companhiaaerea.utils.Console.lerString;

/**
 * Classe que contém métodos para guardar e carregar dados de ficheiros.
 *
 * @author Francisco Silva
 */
public class File {
    /**
     * Constante que guarda a localização do diretório "home" do utilizador.
     */
    public static final String USER_HOME = System.getProperty("user.home");

    /**
     * Constante que guarda a localização da pasta em que o programa guarda os ficheiros.
     */
    public static final String PATH_PASTA = USER_HOME + "/companhia_aerea";

    /**
     * Método para guardar a lista de voos num ficheiro.
     *
     * @param vooList lista de voos a guardar
     */
    public static void guardarEmFicheiro(ArrayList<Voo> vooList) {
        if (voos.isEmpty()) {
            System.out.println("Não existem voos para guardar.");
            return;
        }

        String nomeFicheiro = lerString("Insira o nome do ficheiro: ");

        try {
            java.io.File pasta = new java.io.File(PATH_PASTA);

            if (!pasta.exists() && !pasta.mkdirs()) {
                System.out.println("Erro a criar pasta. A criar pasta no diretório do projeto.");
                pasta = new java.io.File("");
                if (!pasta.mkdirs()) {
                    System.out.println("Erro a criar pasta.");
                    return;
                }
            }

            String pathFicheiro = PATH_PASTA + "/" + nomeFicheiro + ".voos";
            java.io.File ficheiro = new java.io.File(pathFicheiro);

            BufferedWriter writer = new BufferedWriter(new FileWriter(ficheiro));
            for (Voo voo : vooList) {
                writer.write(voo.getNumVoo() + ";");

                writer.write(voo.getAviao().getNumRegisto() + ";");
                writer.write(voo.getAviao().getMarcaModelo() + ";");
                writer.write(voo.getAviao().getNumLugaresTuristica() + ";");
                writer.write(voo.getAviao().getNumLugaresExecutiva() + ";");

                writer.write(voo.getAeroportoOrigem().getSigla() + ";");
                writer.write(voo.getAeroportoOrigem().getPais() + ";");
                writer.write(voo.getAeroportoOrigem().getCidadeMaisProxima() + ";");

                writer.write(voo.getAeroportoDestino().getSigla() + ";");
                writer.write(voo.getAeroportoDestino().getPais() + ";");
                writer.write(voo.getAeroportoDestino().getCidadeMaisProxima() + ";");

                writer.write(voo.getDataHoraPartida().toString() + ";");
                writer.write(voo.getDataHoraChegada().toString() + ";");

                for (Passageiro passageiro : voo.getPassageiros()) {
                    writer.write(passageiro.getNumIdentificacao() + ";");
                    writer.write(passageiro.getNome() + ";");
                    writer.write(passageiro.getNacionalidade() + ";");
                    writer.write(passageiro.getMorada() + ";");
                    writer.write(passageiro.getTelefone() + ";");
                    writer.write(passageiro.getDataNascimento().toString() + ";");
                    writer.write(passageiro.getEmail() + ";");
                    writer.write(passageiro.getTipoBilhete() + ";");
                    writer.write(passageiro.getLugarReservado() + ";");
                }

                writer.write("PilotoTripulante" + ";");

                for (Tripulante tripulante : voo.getTripulacao()) {
                    writer.write(tripulante.getNumIdentificacao() + ";");
                    writer.write(tripulante.getNome() + ";");
                    writer.write(tripulante.getNacionalidade() + ";");
                    writer.write(tripulante.getMorada() + ";");
                    writer.write(tripulante.getTelefone() + ";");
                    writer.write(tripulante.getDataNascimento().toString() + ";");
                    writer.write(tripulante.getNumLicensa() + ";");
                    writer.write(tripulante.getDataValidadeLicensa().toString() + ";");
                    writer.write(tripulante.getAnotacoes() + ";");
                    writer.write(tripulante.getCategoria() + ";");
                }

                writer.newLine();
            }

            writer.close();

            System.out.println("Ficheiro guardado com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro a guardar ficheiro: " + e);
        }
    }

    /**
     * Lê uma lista de voos de um arquivo especificado pelo nome do ficheiro.
     * O ficheiro deve estar no formato especificado, contendo informações sobre voo, avião, aeroportos, passageiros e tripulação, separadas por ";".
     *
     * @param fileName o nome do ficheiro a ser lido.
     * @return uma lista de objeto Voo, populados com as informações lidas do arquivo.
     */
    public static ArrayList<Voo> readVooListFromFile(String fileName) {
        String pathFicheiro = PATH_PASTA + "/" + fileName;

        ArrayList<Voo> vooList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(pathFicheiro));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                int index = 0;

                Voo voo = new Voo();
                voo.setNumVoo(parts[index++]);

                Aviao aviao = new Aviao();
                aviao.setNumRegisto(parts[index++]);
                aviao.setMarcaModelo(parts[index++]);
                aviao.setNumLugaresTuristica(Integer.parseInt(parts[index++]));
                aviao.setNumLugaresExecutiva(Integer.parseInt(parts[index++]));
                voo.setAviao(aviao);

                Aeroporto aeroportoOrigem = new Aeroporto();
                aeroportoOrigem.setSigla(parts[index++]);
                aeroportoOrigem.setPais(parts[index++]);
                aeroportoOrigem.setCidadeMaisProxima(parts[index++]);
                voo.setAeroportoOrigem(aeroportoOrigem);

                Aeroporto aeroportoDestino = new Aeroporto();
                aeroportoDestino.setSigla(parts[index++]);
                aeroportoDestino.setPais(parts[index++]);
                aeroportoDestino.setCidadeMaisProxima(parts[index++]);
                voo.setAeroportoDestino(aeroportoDestino);

                voo.setDataHoraPartida(LocalDateTime.parse(parts[index++]));
                voo.setDataHoraChegada(LocalDateTime.parse(parts[index++]));

                ArrayList<Passageiro> passageiros = new ArrayList<>();
                while (!parts[index].equals("PilotoTripulante")) {
                    Passageiro passageiro = new Passageiro();

                    passageiro.setNumIdentificacao(Integer.parseInt(parts[index++]));
                    passageiro.setNome(parts[index++]);
                    passageiro.setNacionalidade(parts[index++]);
                    passageiro.setMorada(parts[index++]);
                    passageiro.setTelefone(Integer.parseInt(parts[index++]));
                    passageiro.setDataNascimento(LocalDate.parse(parts[index++]));
                    passageiro.setEmail(parts[index++]);
                    passageiro.setTipoBilhete(Passageiro.TipoBilhete.valueOf(parts[index++]));
                    passageiro.setLugarReservado(parts[index++]);
                    passageiros.add(passageiro);
                }
                voo.setPassageiros(passageiros);

                ArrayList<Tripulante> tripulacao = new ArrayList<>();

                index++;

                while (index < parts.length) {
                    Tripulante tripulante = new Tripulante();
                    tripulante.setNumIdentificacao(Integer.parseInt(parts[index++]));
                    tripulante.setNome(parts[index++]);
                    tripulante.setNacionalidade(parts[index++]);
                    tripulante.setMorada(parts[index++]);
                    tripulante.setTelefone(Integer.parseInt(parts[index++]));
                    tripulante.setDataNascimento(LocalDate.parse(parts[index++]));
                    tripulante.setNumLicensa(Integer.parseInt(parts[index++]));
                    tripulante.setDataValidadeLicensa(LocalDate.parse(parts[index++]));
                    tripulante.setAnotacoes(parts[index++]);
                    tripulante.setCategoria(Enum.valueOf(Tripulante.Categoria.class, parts[index++]));
                    tripulacao.add(tripulante);
                }
                voo.setTripulacao(tripulacao);
                vooList.add(voo);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vooList;
    }
}
