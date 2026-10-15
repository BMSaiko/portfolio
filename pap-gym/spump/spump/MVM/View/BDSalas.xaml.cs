using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Shapes;

namespace spump.MVM.View
{
    /// <summary>
    /// Lógica interna para BDSalas.xaml
    /// </summary>
    public partial class BDSalas : Window
    {
        string Nome="";
        int NumeroSala = 0, Limite = 0, But = 0;

        public BDSalas(int numeroSala, string nome, int limite, int buT)
        {
            InitializeComponent();

            NumeroSala = numeroSala;
            Nome = nome;
            Limite = limite;
            But = buT;

            if (But == 2)
            {
                nomeTxt.Text = Nome;
                limiteTxt.Text = Limite.ToString();
            }
        }

        private void closeWindow_Click(object sender, RoutedEventArgs e)
        {
            this.Close();
        }

        private void butConfirm_Click(object sender, RoutedEventArgs e)
        {
            BD b = new BD();
            b.LigaBD();

            try
            {
                Nome = nomeTxt.Text.ToString();
                Limite = int.Parse(limiteTxt.Text);



                if (But == 1 && Nome != "" && Limite != 0)
                {
                    try
                    {
                        string query = string.Format("insert into salas values (null,'{0}','{1}');", Nome, Limite); // o número não leva pelicas
                        //MessageBox.Show(query);
                        MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                        comandoMySQL.ExecuteNonQuery();
                        MessageBox.Show("Sala " + Nome + " inserido com sucesso.", "Salas");
                        b.FechaBD();
                    }
                    catch
                    {
                        MessageBox.Show("Operação Falhada!", "Erro");
                    }
                    this.Close();
                }
                else if (But == 2 && Nome != "" && Limite != 0)
                {
                    try
                    {
                        string query = string.Format("UPDATE salas SET nome='{0}', limite ='{1}' WHERE NumeroSala={2};", Nome, Limite, NumeroSala);
                        //MessageBox.Show(query);
                        MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                        comandoMySQL.ExecuteNonQuery();
                        MessageBox.Show("Sala " + Nome + " inserido com sucesso.", "Salas");
                        b.FechaBD();
                    }
                    catch
                    {
                        MessageBox.Show("ERRO: Dados não atualizados!...", "Salas");
                    }
                    this.Close();
                }
                else
                {
                    MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Salas");
                }
            }
            catch
            {
                MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Salas");
            }
            
        }
    }
}
