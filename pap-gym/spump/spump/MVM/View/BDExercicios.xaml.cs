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
    /// Lógica interna para BDExercicios.xaml
    /// </summary>
    public partial class BDExercicios : Window
    {
        string Nome, Descanso;
        int CodEx = 0, Series = 0, Repeticoes = 0, But = 0;

        public BDExercicios(int codEx, string nome, int series, int repeticoes, string descanso, int buT)
        {
            InitializeComponent();
            PreencheComboSeries();
            PreencheComboRepeticoes();
            PreencheComboDescanso();

            CodEx = codEx;
            Nome = nome;
            Series = series;
            Repeticoes = repeticoes;
            Descanso = descanso;
            But = buT;

            if (But == 2)
            {
                nomeTxt.Text = Nome;
                seriesTxt.Text = Series.ToString();
                repeticoesTxt.Text = Repeticoes.ToString();
                descansoTxt.Text = Descanso; 
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
                Series = int.Parse(seriesTxt.Text);
                Repeticoes = int.Parse(repeticoesTxt.Text);
                Descanso = descansoTxt.Text;



                if (But == 1 && Nome != "" && Series != 0 && Repeticoes != 0 && Descanso != "")
                {
                    try
                    {
                        string query = string.Format("insert into exercicios values (null,'{0}','{1}','{2}','{3}');", Nome, Series, Repeticoes, Descanso); // o número não leva pelicas
                        //MessageBox.Show(query);
                        MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                        comandoMySQL.ExecuteNonQuery();
                        MessageBox.Show("Exercicio " + Nome + " inserido com sucesso.", "Exercícios");
                        b.FechaBD();
                    }
                    catch
                    {
                        MessageBox.Show("Operação Falhada!", "Erro");
                    }
                    this.Close();
                }
                else if (But == 2 && Nome != "" && Series != 0 && Repeticoes != 0 && Descanso != "")
                {
                    try
                    {
                        string query = string.Format("UPDATE exercicios SET nome='{0}', series ='{1}', repeticoes ='{2}', descanso='{3}' WHERE CodEx={4};", Nome, Series, Repeticoes, Descanso, CodEx);
                        //MessageBox.Show(query);
                        MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                        comandoMySQL.ExecuteNonQuery();
                        MessageBox.Show("Exercício " + Nome + " alterado com sucesso!", "Exercícios");
                        b.FechaBD();
                    }
                    catch
                    {
                        MessageBox.Show("ERRO: Dados não atualizados!...", "Exercícios");
                    }
                    this.Close();
                }
                else
                {
                    MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Exercícios");
                }
            }
            catch
            {
                MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Exercícios");
            }

            
            
        }

        public void PreencheComboSeries()
        {
            BD b = new BD();
            b.LigaBD();
            MySqlDataReader reader = null;
            seriesTxt.Items.Clear();
            seriesTxt.IsEditable = false; //Evitar edições não autorizadas na combo box
            try
            {
                String query = @"SELECT distinct series from exercicios order by series asc;";
                MySqlCommand cmd = new MySqlCommand(query, b.con);
                reader = cmd.ExecuteReader(); //executa o reader
                while (reader.Read())
                {
                    seriesTxt.Items.Add(reader[0]); //Cada item pode ser composto por um ou mais campos 
                }
            }
            catch
            {
                MessageBox.Show("Algo não correu bem!");
            }
            b.FechaBD();
        }

        public void PreencheComboRepeticoes()
        {
            repeticoesTxt.Items.Add(8);
            repeticoesTxt.Items.Add(10);
            repeticoesTxt.Items.Add(12);
            repeticoesTxt.Items.Add(15);
            repeticoesTxt.Items.Add(20);
        }

        public void PreencheComboDescanso()
        {
            BD b = new BD();
            b.LigaBD();
            MySqlDataReader reader = null;
            descansoTxt.Items.Clear();
            descansoTxt.IsEditable = false; //Evitar edições não autorizadas na combo box
            try
            {
                String query = @"SELECT distinct descanso from exercicios order by descanso asc;";
                MySqlCommand cmd = new MySqlCommand(query, b.con);
                reader = cmd.ExecuteReader(); //executa o reader
                while (reader.Read())
                {
                    descansoTxt.Items.Add(reader[0]); //Cada item pode ser composto por um ou mais campos 
                }
            }
            catch
            {
                MessageBox.Show("Algo não correu bem!");
            }
            b.FechaBD();
        }

    }
}
