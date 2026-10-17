package org.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static ArrayList<Voo> voos;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("-------------------------------");
        System.out.println("1 - Adicionar voo.");
        System.out.println("2 - Remover voo.");
        System.out.println("3 - Pesquisar voo - por rota e data.");
        System.out.println("4 - Mostrar passageiros de um voo.");
        System.out.println("5 - Alterar lista de passageiros de um voo.");
        System.out.println("6 - Pesquisar voos num intervalo de datas.");
        System.out.println("7 - Voo com maior número de passageiros transportados num intervalo de\n" +
                "datas.");
        System.out.println("8 - Total de passageiros entre datas.");
        System.out.println("9 - Média de passageiros numa determinada rota e datas.");
        System.out.println("10 - Guardar ficheiro.");
        System.out.println("11 - Ler ficheiro.");
        System.out.println("-------------------------------");

        int i = scanner.nextInt();

        switch (i) {
            case 1:
                System.out.println(adicionarVoo());
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            case 9:
                break;
            case 10:
                break;
            case 11:
                break;
            case 12:
                break;
            default:
                System.out.println("Opção inválida.");
                break;
        }
    }

    static Voo adicionarVoo() {
        Voo voo = new Voo(lerNumVoo(), lerAviao(), lerAeroporto(), lerAeroporto(), lerDataHora(), lerDataHora(), lerPassageiros(), lerTripulacao());

        voos.add(voo);

        return voo;
    }

    static String lerNumVoo() {
        System.out.println("Insira o número do voo: ");
        return scanner.next();
    }

    static Aviao lerAviao() {
        System.out.println("Insira agora os dados do avião: ");
        System.out.println("- Insira o número de registo: ");
        String numRegisto = scanner.next();
        System.out.println("- Insira a marca e modelo (marca-modelo): ");
        String marcaModelo = scanner.next();
        System.out.println("- Insira o número de lugares na classe turistica: ");
        int numLugaresTuristica = scanner.nextInt();
        System.out.println("- Insira o número de lugares na classe executiva: ");
        int numLugaresExecutiva = scanner.nextInt();

        return new Aviao(numRegisto, marcaModelo, numLugaresTuristica, numLugaresExecutiva);
    }

    static Aeroporto lerAeroporto() {
        System.out.println("Insira a sigla: ");
        String sigla = scanner.next();
        System.out.println("Insira o país: ");
        String pais = scanner.next();
        System.out.println("Insira a cidade mais próxima: ");
        String cidadeMaisProx = scanner.next();

        return new Aeroporto(sigla, pais, cidadeMaisProx);
    }

    static LocalDateTime lerDataHora() {
        System.out.println("Insira a data e tempo (dd-MM-yyyy HH:mm): ");
        String dataHoraString = scanner.next("");

        return LocalDateTime.parse(dataHoraString,
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }

    static ArrayList<Passageiro> lerPassageiros() {
        ArrayList<Passageiro> passageiros = new ArrayList<>();

        while (true) {
            System.out.println("Insira os dados do passageiro (0 para terminar): ");
            System.out.println("- Insira o número de identificação: ");
            int numIdentificacao = scanner.nextInt();
            if (numIdentificacao == 0) {
                break;
            }
            System.out.println("- Insira o nome: ");
            String nome = scanner.next();
            System.out.println("- Insira a nacionalidade: ");
            String nacionalidade = scanner.next();
            System.out.println("- Insira a morada: ");
            String morada = scanner.next();
            System.out.println("- Insira o número de telefone: ");
            int telefone = scanner.nextInt();
            System.out.println("- Insira a data de nascimento (AAAA-MM-DD): ");
            LocalDate dataNascimento = LocalDate.parse(scanner.next());
            System.out.println("- Insira o email: ");
            String email = scanner.next();
            System.out.println("- Insira o tipo de bilhete (1 - Turística, 2 - Executiva): ");
            Passageiro.TipoBilhete tipoBilhete = scanner.nextInt() == 1 ? Passageiro.TipoBilhete.TURISTICA : Passageiro.TipoBilhete.EXECUTIVA;
            System.out.println("- Insira o lugar do passageiro: ");
            String lugarReservado = scanner.next();

            passageiros.add(new Passageiro(numIdentificacao, nome, nacionalidade, morada, telefone, dataNascimento, email, tipoBilhete, lugarReservado));
        }

        return passageiros;
    }

    static ArrayList<PilotoTripulante> lerTripulacao() {
        ArrayList<PilotoTripulante> tripulantes = new ArrayList<>();

        while (true) {
            System.out.println("Insira os dados do tripulante (0 para terminar): ");
            System.out.println("- Insira o número de identificação: ");
            int numIdentificacao = scanner.nextInt();
            if (numIdentificacao == 0) {
                break;
            }
            System.out.println("- Insira o nome: ");
            String nome = scanner.next();
            System.out.println("- Insira a nacionalidade: ");
            String nacionalidade = scanner.next();
            System.out.println("- Insira a morada: ");
            String morada = scanner.next();
            System.out.println("- Insira o número de telefone: ");
            int telefone = scanner.nextInt();
            System.out.println("- Insira a data de nascimento (AAAA-MM-DD): ");
            LocalDate dataNascimento = LocalDate.parse(scanner.next());
            System.out.println("- Insira o número de licença: ");
            int numLicenca = scanner.nextInt();
            System.out.println("- Insira a data de validade da licença (AAAA-MM-DD): ");
            LocalDate dataValidadeLicenca = LocalDate.parse(scanner.next());
            System.out.println("- Insira anotações sobre o tripulante: ");
            String anotacoes = scanner.next();

            System.out.println("- Insira o tipo de tripulante (1 - Piloto, 2 - Tripulante): ");
            PilotoTripulante.Categoria categoria = scanner.nextInt() == 1 ? PilotoTripulante.Categoria.PILOTO : PilotoTripulante.Categoria.TRIPULANTE;

            PilotoTripulante tripulante = new PilotoTripulante(numIdentificacao, nome, nacionalidade, morada, telefone, dataNascimento, numLicenca, dataValidadeLicenca, anotacoes, categoria);
        }

        return tripulantes;
    }
}
