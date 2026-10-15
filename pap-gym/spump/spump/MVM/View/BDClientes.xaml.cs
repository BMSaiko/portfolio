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
    /// Lógica interna para BDClientes.xaml
    /// </summary>
    public partial class BDClientes : Window
    {
        int codCliente = 0, But=0;
        string Nome = "", DataN = "", Email = "";

        private void closeWindow_Click(object sender, RoutedEventArgs e)
        {
            this.Close();
        }

        string Contacto = "";


        public BDClientes(int codC, string nome, string dataN, string email, string contacto, int buT)
        {
            InitializeComponent();
            codCliente = codC;
            Nome = nome;
            DataN = dataN;
            Email = email;
            Contacto = contacto;
            But = buT;

            if(But == 2)
            {
                nomeTxt.Text = Nome;
                dataTxt.Text = DataN;
                emailTxt.Text = Email;
                contactoTxt.Text = Contacto;
            }
        }

        private void butConfirm_Click(object sender, RoutedEventArgs e)
        {
            BD b = new BD();
            b.LigaBD();
            if(dataTxt.SelectedDate == null)
            {
                MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Professores");
            }
            else
            {
                Nome = nomeTxt.Text.ToString();
                DataN = dataTxt.SelectedDate.Value.ToString("yyyy-MM-dd");
                Email = emailTxt.Text;
                Contacto = contactoTxt.Text.ToString();



                if (But == 1 && Nome != "" && DataN != "" && Email != "" && Contacto != "")
                {
                    try
                    {
                        string query = string.Format("insert into clientes values (null,'{0}','{1}','{2}','{3}');", Nome, DataN, Email, Contacto); // o número não leva pelicas
                        //MessageBox.Show(query);
                        MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                        comandoMySQL.ExecuteNonQuery();
                        MessageBox.Show("Cliente " + Nome + " inserido com sucesso.");
                        b.FechaBD();
                    }
                    catch
                    {
                        MessageBox.Show("Operação Falhada!");
                    }
                    this.Close();
                }
                else if (But == 2 && Nome != "" && DataN != "" && Email != "" && Contacto != "")
                {
                    try
                    {
                        string query = string.Format("UPDATE clientes SET nome='{0}', dataNascimento ='{1}', email ='{2}', contacto='{3}' WHERE CodC={4};", Nome, DataN, Email, Contacto, codCliente);
                        //MessageBox.Show(query);
                        MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                        comandoMySQL.ExecuteNonQuery();
                        MessageBox.Show("Cliente " + Nome + " alterado com sucesso!", "Clientes");
                        b.FechaBD();
                    }
                    catch
                    {
                        MessageBox.Show("ERRO: Dados não atualizados!...", "Clientes");
                    }
                    this.Close();
                }
                else
                {
                    MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Clientes");
                }
            }
        }
    }
}
