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
    /// Interaction logic for BDProfessores.xaml
    /// </summary>
    public partial class BDProfessores : Window
    {
        int codProfessor=0, But=0;
        string Nome="", DataN="", Contacto="";
        double Salario=0;

        public void PontosEmVezDeVirgulas()
        {
            Thread.CurrentThread.CurrentCulture = new CultureInfo("en-US");
            Thread.CurrentThread.CurrentUICulture = new CultureInfo("en-US");
        }

        public BDProfessores(int codP, string nome, string dataN, double salario, string contacto, int  but)
        {
            InitializeComponent();
            PontosEmVezDeVirgulas();

            codProfessor = codP;
            Nome = nome;
            DataN = dataN;
            Salario = salario;
            Contacto = contacto;
            But = but;

            if (But == 2)
            {
                nomeTxt.Text = Nome;
                dataTxt.Text = DataN;
                salarioTxt.Text = Salario.ToString();
                contactoTxt.Text = Contacto;
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
            if (dataTxt.SelectedDate == null)
            {
                MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Professores");
            }
            else
            {
                Nome = nomeTxt.Text.ToString();
                DataN = dataTxt.SelectedDate.Value.ToString("yyyy-MM-dd");
                Salario = double.Parse(salarioTxt.Text);
                Contacto = contactoTxt.Text.ToString();



                if (But == 1 && Nome != "" && DataN != "" && Salario != 0 && Contacto != "")
                {
                    try
                    {
                        string query = string.Format("insert into professores values (null,'{0}','{1}','{2}','{3}');", Nome, DataN, Contacto, Salario); // o número não leva pelicas
                        //MessageBox.Show(query);
                        MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                        comandoMySQL.ExecuteNonQuery();
                        MessageBox.Show("Professor " + Nome + " inserido com sucesso.", "Professores");
                        b.FechaBD();
                    }
                    catch
                    {
                        MessageBox.Show("Operação Falhada!");
                    }
                    this.Close();
                }
                else if (But == 2 && Nome != "" && DataN != "" && Salario != 0 && Contacto != "")
                {
                    try
                    {
                        string query = string.Format("UPDATE professores SET nome = '{0}', dataNascimento = '{1}', contacto ='{2}', salario = '{3}' WHERE CodP={4};", Nome, DataN, Contacto, Salario, codProfessor);
                        //MessageBox.Show(query);
                        MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                        comandoMySQL.ExecuteNonQuery();
                        MessageBox.Show("Professor " + Nome + " alterado com sucesso!", "Professores");
                        b.FechaBD();
                    }
                    catch
                    {
                        MessageBox.Show("ERRO: Dados não atualizados!...", "Professores");
                    }
                    this.Close();
                }
                else
                {
                    MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Professores");
                }
            }
        }
    }
}
