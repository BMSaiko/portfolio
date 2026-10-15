using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Data;
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
    /// Interaction logic for BDReservas.xaml
    /// </summary>
    public partial class BDReservas : Window
    {
        int codC=0, codAula = 0;
        string nome = "";
        int limite = 0;
        int i = 0;
        int last = 0;
        public BDReservas()
        {
            InitializeComponent();
            CarregaDadosGridClientes();
            CarregaDadosGridAulas();
        }

        private void CarregaDadosGridClientes()
        {
            BD b = new BD();
            b.LigaBD();
            try
            {
                DataTable dt = new DataTable();
                string query = string.Format("SELECT CodC, nome FROM clientes;");
                MySqlDataAdapter da = new MySqlDataAdapter(query, b.con);
                da.Fill(dt);
                dataClientes.ItemsSource = dt.DefaultView;// Área para fazer algo mais com a grid view...
            }
            catch
            {
                MessageBox.Show("Não foram retornados dados!", "ERRO");
            }
            b.FechaBD();
        }

        private void CarregaDadosGridAulas()
        {
            BD b = new BD();
            b.LigaBD();
            try
            {
                DataTable dt = new DataTable();
                string query = string.Format("SELECT * FROM aulagrupo;");
                MySqlDataAdapter da = new MySqlDataAdapter(query, b.con);
                da.Fill(dt);
                dataAulas.ItemsSource = dt.DefaultView;// Área para fazer algo mais com a grid view...
            }
            catch
            {
                MessageBox.Show("Não foram retornados dados!", "ERRO");
            }
            b.FechaBD();
        }

        private void search_TextChanged(object sender, TextChangedEventArgs e)
        {

        }

        private void CloseMainwindow_Click(object sender, RoutedEventArgs e)
        {
            this.Close();
        }

        private void dataClientes_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            try
            {
                if (dataClientes.SelectedItem != null)
                {
                    DataRowView v = (DataRowView)dataClientes.SelectedItem;
                    codC = int.Parse(v[0].ToString());
                    nome = v[1].ToString();
                }
            }
            catch
            {

            }
        }

        private void DataClientes_AutoGeneratingColumn(object sender, DataGridAutoGeneratingColumnEventArgs e)
        {
            if (e.PropertyName == "dataNascimento")// Atenção ao campo da datagrid! Neste exemplo chama-se dataF.
            {
                DataGridTextColumn coluna = e.Column as DataGridTextColumn;
                Binding binding = coluna.Binding as Binding;
                binding.StringFormat = "yyyy-MM-dd";

            }
        }

        private void TextBlock_MouseDown(object sender, MouseButtonEventArgs e)
        {
            Foreground = Brushes.Red; 
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            if (i<limite)
            {
                BD b = new BD();
                b.LigaBD();
                try
                {
                    string query = string.Format("insert into clientesporaula values (null,'{0}','{1}');", codC, codAula); // o número não leva pelicas
                    MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                    comandoMySQL.ExecuteNonQuery();
                    i++;
                    //MessageBox.Show(last.ToString());
                    capacidade.Text = "" + last++ + "/" + limite + "";
                    MessageBox.Show("Cliente " + nome + " inscrito com sucesso.", "Reservas");
                    b.FechaBD();
                }
                catch
                {
                    MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Reservas");
                }
            }
            else
            {
                MessageBox.Show("Reserva Inválida!\nEsta aula está cheia.", "Reservas");
            }
        }

        private void dataAulas_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            try
            {
                if (dataAulas.SelectedItem != null)
                {
                    DataRowView v = (DataRowView)dataAulas.SelectedItem;
                    codAula = int.Parse(v[0].ToString());
                    int i = 0;
                    last = 0;
                    BD b = new BD();

                    try
                    {
                        b.LigaBD();
                        MySqlDataReader reader = null;
                        string query = "SELECT limite FROM salas s, aulagrupo a WHERE a.CodAula='"+ codAula +"' and s.NumeroSala = a.NumeroSala";
                        //MessageBox.Show(query);
                        MySqlCommand cmd = new MySqlCommand(query, b.con);
                        reader = cmd.ExecuteReader(); //executar o reader
                        while (reader.Read())
                        {
                            limite = int.Parse(reader[0].ToString());
                            capacidade.Text = "0/" + limite + "";
                        }
                        b.FechaBD();

                        b.LigaBD();
                        MySqlDataReader reader1 = null;
                        string query1 = "SELECT c.CodC FROM clientesporaula c, aulagrupo a WHERE a.CodAula='" + codAula + "' and c.CodAula = a.CodAula";
                        //MessageBox.Show(query1);

                        MySqlCommand cmd1 = new MySqlCommand(query1, b.con);
                        reader1 = cmd1.ExecuteReader(); //executar o reader
                        while (reader1.Read())
                        {

                            capacidade.Text = ""+ i +"/" + limite + "";
                            i++;
                            last = i;
                            //MessageBox.Show("Cliente "+ nome +" ");
                        }
                        b.FechaBD();

                    }
                    catch
                    {
                        MessageBox.Show("Dados Inválidos!");
                    }
                }
            }
            catch
            {

            }
        }
    }
}
