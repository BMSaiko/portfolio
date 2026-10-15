using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading;
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
    /// Interaction logic for BDModalidades.xaml
    /// </summary>
    public partial class BDModalidades : Window
    {
        int codModalidades = 0, But = 0;
        string Nome = "";
        double Valor = 0;

        public void PontosEmVezDeVirgulas()
        {
            Thread.CurrentThread.CurrentCulture = new CultureInfo("en-US");
            Thread.CurrentThread.CurrentUICulture = new CultureInfo("en-US");
        }

        public BDModalidades(int codM, string nome, double valor, int but)
        {
            InitializeComponent();
            PontosEmVezDeVirgulas();

            codModalidades = codM;
            Nome = nome;
            Valor = valor;
            But = but;

            if (But == 2)
            {
                nomeTxt.Text = Nome;
                valorTxt.Text = Valor.ToString();
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
                Valor = double.Parse(valorTxt.Text);

                if (But == 1 && Nome != "" && Valor != 0)
                {
                    try
                    {
                        string query = string.Format("insert into modalidades values (null,'{0}','{1}');", Nome, Valor); // o número não leva pelicas
                        //MessageBox.Show(query);
                        MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                        comandoMySQL.ExecuteNonQuery();
                        MessageBox.Show("Modalidade " + Nome + " inserido com sucesso.", "Modalidades");
                        b.FechaBD();
                    }
                    catch
                    {
                        MessageBox.Show("Operação Falhada!");
                    }
                    this.Close();
                }
                else if (But == 2 && Nome != "" && Valor != 0)
                {
                    try
                    {
                        string query = string.Format("UPDATE modalidades SET nome = '{0}', valor = '{1}' WHERE CodM={2};", Nome, Valor, codModalidades);
                        //MessageBox.Show(query);
                        MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                        comandoMySQL.ExecuteNonQuery();
                        MessageBox.Show("Modalidade " + Nome + " alterado com sucesso!", "Modalidades");
                        b.FechaBD();
                    }
                    catch
                    {
                        MessageBox.Show("ERRO: Dados não atualizados!...", "Modalidades");
                    }
                    this.Close();
                }
                else
                {
                    MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Modalidades");
                }
            }
            catch
            {
                MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Modalidades");
            }
                
            
        }
    }
}
