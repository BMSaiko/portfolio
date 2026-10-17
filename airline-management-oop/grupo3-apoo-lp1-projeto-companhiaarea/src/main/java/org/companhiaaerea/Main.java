/**
 * Classe principal do programa, que contém o menu principal e as ações correspondentes.
 * Possui também uma lista de objetos do tipo Voo, usada para armazenar os voos em memória.
 */

package org.companhiaaerea;

import org.companhiaaerea.utils.Data;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;

import static org.companhiaaerea.utils.Console.*;
import static org.companhiaaerea.utils.Data.lerData;
import static org.companhiaaerea.utils.File.*;

/**
 * Classe principal do programa, que contém o menu principal e as ações correspondentes.
 * Possui também uma lista de objetos do tipo Voo, usada para armazenar os voos em memória.
 *
 * @author Francisco Silva
 * @author Bruno Silva
 * @author Ricardo Pereira
 */
public class Main {
    /**
     * Lista de voos adicionados.
     */
    public static ArrayList<Voo> voos = new ArrayList<>();

    /**
     * Método principal do programa. Exibe um menu com as seguintes opções:
     * 1 — Adicionar voo
     * 2 — Retirar voo
     * 3 — Pesquisar voos
     * 4 — Voo com maior número de passageiros num intervalo de datas
     * 5 — Apresentar lista de passageiros de voos numa data
     * 6 — Alterar lista de passageiros de um voo
     * 7 — Apresentar média de passageiros de voos num intervalo de datas
     * 8 — Total de passageiros num intervalo de datas
     * 9 — Guardar informação num ficheiro
     * 10 — Ler informação de um ficheiro
     * 0 — Sair do programa
     *
     * @param args Argumentos passados para o programa (não utilizado)
     */
    public static void main(String[] args) {
        int i;

        while (true) {
            System.out.println("------------------------------- Menu Principal -------------------------------");
            System.out.println("1\tAdicionar voo.");
            System.out.println("2\tRetirar voo.");
            System.out.println("3\tPesquisar voos.");
            System.out.println("4\tVoo com maior número de passageiros num intervalo de datas.");
            System.out.println("5\tApresentar lista de passageiros de voos numa data.");
            System.out.println("6\tAlterar lista de passageiros de um voo.");
            System.out.println("7\tApresentar média de passageiros de voos num intervalo de datas.");
            System.out.println("8\tTotal de passageiros num intervalo de datas.");
            System.out.println("------------------------------- Guardar / Ler --------------------------------");
            System.out.println("9\tGuardar informação num ficheiro.");
            System.out.println("10\tLer informação de um ficheiro.");
            System.out.println("------------------------------------------------------------------------------");
            System.out.println(TEXTO_VERMELHO + "0\tSair." + RESET_COR_TEXTO);
            System.out.println("------------------------------------------------------------------------------");

            i = lerInt("Opção: ", "Insira um número válido.");

            switch (i) {
                case 1 -> adicionarVoo();
                case 2 -> retirarVoo();
                case 3 -> submenuPesquisar();
                case 4 -> pesquisarVooMaisPassageirosData();
                case 5 -> pesquisarListaPassageirosData();
                case 6 -> alterarPassageirosVoo();
                case 7 -> mediaPassageiros();
                case 8 -> totalPassageiros();
                case 9 -> guardarEmFicheiro(voos);
                case 10 -> {
                    // Encontra pasta
                    File pasta = new File(PATH_PASTA);
                    File[] listaFicheiros = pasta.listFiles();

                    // Retorna se a pasta não conter ficheiros
                    if (listaFicheiros == null || listaFicheiros.length == 0) {
                        System.out.println("Não existem ficheiros.");
                        break;
                    }

                    // Mostra todos os ficheiros numerados para selecionar
                    int cont = 1;
                    for (File ficheiro : listaFicheiros) {
                        if (ficheiro.isFile()) {
                            System.out.println(cont + " - " + ficheiro.getName());
                            cont++;
                        }
                    }

                    int numFicheiro = lerInt("Insira o número do ficheiro: ", "Insira um número válido.");

                    File ficheiroSelecionado = listaFicheiros[numFicheiro - 1];
                    String nomeFicheiro = ficheiroSelecionado.getName();

                    voos = readVooListFromFile(nomeFicheiro);
                    System.out.println("Voos lidos com sucesso.");
                }
                case 0 -> {
                    if (voos.isEmpty()) System.exit(0); // Termina o programa

                    String res = lerString(
                            "Pode ter informação por guardar. Deseja guardar num ficheiro? (y/n): ",
                            "[YNyn]",
                            "Insira uma resposta válida."
                    );

                    // Verifica se a resposta é y (yes/sim)
                    if (res.equalsIgnoreCase("y")) {
                        guardarEmFicheiro(voos);
                        return;
                    }

                    System.exit(0);
                }

                default -> System.out.println("Opção inválida.");
            }
        }
    }

    /**
     * Adiciona um voo a lista de voos
     */
    static void adicionarVoo() {
        Voo novoVoo = Voo.ler();

        voos.add(novoVoo);

        System.out.println("Voo adicionado com sucesso.");
    }

    /**
     * Exibe o submenu de pesquisar
     */
    static void submenuPesquisar() {
        clearConsole();

        if (voos.isEmpty()) {
            System.out.println("Não existem voos para pesquisar.");
            return;
        }

        System.out.println("----------------- Pesquisar -----------------");
        System.out.println("1. Pesquisar por intervalo de data e rota.");
        System.out.println("2. Pesquisar por intervalo de datas.");
        System.out.println("0. Voltar atrás.");
        System.out.println("---------------------------------------------");
        System.out.print("Opção: ");

        int i = scanner.nextInt();

        clearConsole();

        switch (i) {
            case 1 -> pesquisarVooDataRota();
            case 2 -> pesquisarVoosData();
            case 0 -> {
            } // volta
            default -> {
                System.out.println("Opção inválida.");
                submenuPesquisar();
            }
        }
    }

    /**
     * Método para pesquisar voo por data e rota.
     * O utilizado é solicitado a inserir a data de partida e chegada, bem como as siglas dos aeroportos de origem e destino.
     * Os voos que corresponderem às informações inseridas serão adicionados a uma lista auxiliar e exibidos no terminal.
     */
    static void pesquisarVooDataRota() {
        ArrayList<Voo> voosAux = new ArrayList<>();

        LocalDate dataPartida = Data.lerData("Insira a data de partida do Voo (dd/MM/yyyy): ");
        LocalDate dataChegada = Data.lerData("Insira a data de chegada do Voo (dd/MM/yyyy): ");

        String siglaOrigem = lerString("Insira a Sigla do Aeroporto de origem: ");
        String siglaChegada = lerString("Insira a Sigla do Aeroporto de chegada: ");

        for (Voo voo : voos) {
            LocalDate dataPartidaVoo = voo.getDataHoraPartida().toLocalDate();
            LocalDate dataChegadaVoo = voo.getDataHoraChegada().toLocalDate();

            if (!dataPartidaVoo.equals(dataPartida)
                    || !dataChegadaVoo.equals(dataChegada)
            ) {
                break;
            }

            if (!voo.getAeroportoOrigem().getSigla().equals(siglaOrigem)
                    || !voo.getAeroportoDestino().getSigla().equals(siglaChegada)
            ) {
                break;
            }

            voosAux.add(voo);
        }

        System.out.println(voosAux);
    }

    /**
     * Método para pesquisar voos conforme as datas de início e fim inseridas pelo utilizador.
     *
     * @return Retorna uma lista de voos que estão no intervalo de datas inserido.
     */
    static ArrayList<Voo> pesquisarVoosData() {
        ArrayList<Voo> voosAux = new ArrayList<>();

        LocalDate dataInicio = Data.lerData("Insira a data de de inicio (dd/MM/yyyy): ");
        LocalDate dataFim = Data.lerData("Insira a data de fim (dd/MM/yyyy): ");

        for (Voo voo : voos) {
            LocalDate dataPartidaVoo = voo.getDataHoraPartida().toLocalDate();
            LocalDate dataChegadaVoo = voo.getDataHoraChegada().toLocalDate();

            if (dataPartidaVoo.isBefore(dataInicio)
                    || dataChegadaVoo.isAfter(dataFim)
            ) {
                break;
            }

            voosAux.add(voo);
        }

        if (voosAux.isEmpty()) {
            System.out.println("Não existem voos para o intervalo de datas inserido.");
            return voosAux;
        }

        System.out.println(voosAux);

        return voosAux;
    }

    /**
     * Método estático para remover um voo existente.
     * Se a lista de voos estiver vazia, será exibida a mensagem "Não existem voos para eliminar."
     * Caso contrário, o utilizador será solicitado a inserir o número do voo (no formato VXXX) a ser removido.
     * O voo será buscado na lista de voos e, caso encontrado, será removido.
     * Se o voo não for encontrado, será exibida a mensagem "Voo não encontrado: [numVoo]"
     * Se o voo for removido com sucesso, será exibida a mensagem "Voo [numVoo] removido com sucesso."
     */
    static void retirarVoo() {
        if (voos.isEmpty()) {
            System.out.println("\nNão existem voos para eliminar.");
            return;
        }

        LocalDate data = lerData("Insira a data do voo (dd/MM/yyyy): ");
        String numVoo = lerString("Insira número de voo (VXXX): ");

        Voo voo = voos.stream()
                // Filtra a lista de voos pelo número de voo desejado
                .filter(v -> v.getNumVoo().equals(numVoo))
                // Filtra a lista de voos pela data de partida desejada
                .filter(v -> v.getDataHoraPartida().toLocalDate().equals(data))
                // Seleciona o primeiro voo encontrado
                .findFirst()
                // Se não encontrar nenhum voo, retorna null
                .orElse(null);

        if (voo == null) {
            System.out.println("Voo não encontrado: " + numVoo);
            return;
        }

        voos.remove(voo);

        System.out.println("Voo " + numVoo + " removido com sucesso.");
    }


    /**
     * Método estático para alterar o número de passageiros de um voo existente.
     * Se a lista de voos estiver vazia, será exibida a mensagem "Não existem voos para serem alterados!"
     * Caso contrário, o utilizador será solicitado a inserir o número do voo (no formato VXXX) a ser alterado.
     * O voo será buscado na lista de voos e, caso encontrado, será exibido um menu com as opções de adicionar ou remover passageiros.
     * Se o voo não for encontrado, será exibida a mensagem "Voo não encontrado: [numVoo]"
     */
    static void alterarPassageirosVoo() {
        if (voos.isEmpty()) {
            System.out.println("\nNão existem voos para serem alterados!");
            return;
        }

        String numVoo = lerString("Insira número de voo: ");

        System.out.println("1 - Adicionar passageiros");
        System.out.println("2 - Remover passageiros");
        System.out.println("0 - Sair");
        String operacao = lerString("Introduza o número da operação que deseja: ");

        if (operacao.equals("0")) {
            return;
        }

        Iterator<Voo> iterator = voos.iterator();
        Voo valor = iterator.next();

        if (operacao.equals("1")) {
            if (valor.getNumVoo().equals(numVoo)) {
                valor.getPassageiros().addAll(Passageiro.ler());

                System.out.println("Voo alterado com sucesso!");
            } else {
                System.out.println("Esse voo não existe, logo não pode ser removido!");
            }
        } else if (operacao.equals("2")) {
            if (valor.getNumVoo().equals(numVoo)) {
                String nomePassageiro = lerString("Introduza o nome do passageiro para retirar do voo: ");
                Passageiro passageiro = null;

                for (Passageiro p : valor.getPassageiros()) {
                    if (p.getNome().equals(nomePassageiro)) {
                        passageiro = p;
                        break;
                    }
                }

                if (passageiro != null) {
                    valor.getPassageiros().remove(passageiro);
                    System.out.println("Passageiro removido com sucesso!");
                } else {
                    System.out.println("Esse passageiro não está registrado nesse voo!");
                }
            }
        }
    }

    /**
     * Método estático para pesquisar o voo com o maior número de passageiros num determinado período.
     * O utilizador é solicitado a inserir uma data de início e uma data final para a pesquisa.
     * Os voos com data de partida anterior à data de início ou data de chegada posterior à data final são descartados.
     * O voo com o maior número de passageiros é então exibido, incluindo o número do voo e a quantidade de passageiros.
     */
    static void pesquisarVooMaisPassageirosData() {
        if (voos.isEmpty()) {
            System.out.println("\nNão existem voos para pesquisar!");
            return;
        }

        Voo maior = voos.get(0);

        LocalDate dataInicio = lerData("Insira a data de inicio: ");
        LocalDate dataFim = lerData("Insira a data de fim: ");

        for (Voo voo : voos) {
            LocalDate dataPartidaVoo = voo.getDataHoraPartida().toLocalDate();
            LocalDate dataChegadaVoo = voo.getDataHoraPartida().toLocalDate();

            if (!dataPartidaVoo.isAfter(dataInicio)
                    && !dataChegadaVoo.isBefore(dataFim)
            ) break;

            if (voo.getPassageiros().size() > maior.getPassageiros().size())
                maior = voo;
        }

        System.out.println("O Voo com maior número de passageiros é o voo " + maior.getNumVoo() + " com " + maior.getPassageiros().size() + " passageiros.");
    }

    /**
     * Método estático para pesquisar a lista de passageiros de um voo pelo dia da partida.
     * O utilizador será solicitado a inserir a data do voo desejado.
     * Os passageiros do voo com a data de partida correspondente serão exibidos na tela.
     */
    static void pesquisarListaPassageirosData() {
        if (voos.isEmpty()) {
            System.out.println("\nNão existem voos para pesquisar!");
            return;
        }

        LocalDate data = lerData("Insira a data do voo: ");

        for (Voo voo : voos) {
            LocalDate dataPartidaVoo = voo.getDataHoraPartida().toLocalDate();

            if (dataPartidaVoo.equals(data)) {
                System.out.println("Passageiros para o voo " + voo.getNumVoo() + ": ");
                System.out.println(voo.getPassageiros().toString());
            }
        }
    }

    /**
     * Calcula a média de passageiros para uma lista de voos retornada pelo método {@link Main#pesquisarVoosData()}.
     * Imprime o total de passageiros e a média de passageiros para a lista de voos.
     */
    static void mediaPassageiros() {
        if (voos.isEmpty()) {
            System.out.println("\nNão existem voos para pesquisar!");
            return;
        }

        ArrayList<Voo> voosAux = pesquisarVoosData();

        int soma = 0;

        for (Voo voo : voosAux) {
            soma += voo.getPassageiros().size();
        }

        double media = (double) soma / (double) voosAux.size();

        System.out.println("A média de passageiros é: " + String.format("%.2f", media));
    }

    /**
     * Método para calcular o total de passageiros em voos entre duas datas específicas.
     */
    static void totalPassageiros() {
        if (voos.isEmpty()) {
            System.out.println("\nNão existem voos para pesquisar!");
            return;
        }

        LocalDate dataInicio = lerData("Insira a data de inicio (dd/MM/yyyy): ");
        LocalDate dataFim = lerData("Insira a data de fim (dd/MM/yyyy): ");

        int totalPassageiros = 0;

        for (Voo voo : voos) {
            LocalDate dataPartidaVoo = voo.getDataHoraPartida().toLocalDate();
            LocalDate dataChegadaVoo = voo.getDataHoraChegada().toLocalDate();

            if ((dataPartidaVoo.isAfter(dataInicio) || dataPartidaVoo.isEqual(dataInicio)) &&
                    (dataChegadaVoo.isBefore(dataFim) || dataChegadaVoo.isEqual(dataFim))) {
                totalPassageiros += voo.getPassageiros().size();
            }
        }

        System.out.println("Total de passageiros: " + totalPassageiros);
    }
}
