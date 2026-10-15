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
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace spump.MVM.View
{
    /// <summary>
    /// Interação lógica para ClientesView.xam
    /// </summary>
    public partial class ClientesView : UserControl
    {
        string nome="", email="";
        string dataN="";
        string contacto="";
        int codC = 0;

        int buT = 0;
        int verifica = 0;

        public ClientesView()
        {
            InitializeComponent();
            CarregaDadosGrid();
            butEditar.IsEnabled = false;
        }



        private void CarregaDadosGrid()
        {
            BD b = new BD();
            b.LigaBD();
            try
            {
                DataTable dt = new DataTable();
                string query = string.Format("SELECT * FROM clientes;");
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

        private void butInserir_Click(object sender, RoutedEventArgs e)
        {
            verifica = 1;
            buT = 1;
            BDClientes c = new BDClientes(codC, nome, dataN, email, contacto, buT);
            c.Show();
        }

        private void butEditar_Click(object sender, RoutedEventArgs e)
        {
            verifica = 1;
            buT = 2;
            BDClientes c = new BDClientes(codC, nome, dataN, email, contacto, buT);
            c.Show();
        }

        private void SearchClientes_TextChanged(object sender, TextChangedEventArgs e)
        {
            BD b = new BD();
            b.LigaBD();
            try
            {
                DataTable dt = new DataTable();
                string query = string.Format("SELECT * FROM clientes where nome like '" + searchClientes.Text + "%' or email like '" + searchClientes.Text + "%' or contacto like '" + searchClientes.Text + "%' or CodC like '" + searchClientes.Text + "%';");
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

        private void DataClientes_AutoGeneratingColumn(object sender, DataGridAutoGeneratingColumnEventArgs e)
        {
            if (e.PropertyName == "dataNascimento")// Atenção ao campo da datagrid! Neste exemplo chama-se dataF.
            {
                DataGridTextColumn coluna = e.Column as DataGridTextColumn;
                Binding binding = coluna.Binding as Binding;
                binding.StringFormat = "yyyy-MM-dd";

            }
        }

        private void UserControl_MouseMove(object sender, MouseEventArgs e)
        {
            try
            {
                if (Application.Current.Windows.OfType<BDClientes>().Any())
                {

                }
                else
                {
                    if (verifica == 1)
                    {
                        CarregaDadosGrid();
                        verifica = 0;
                    }
                }
            }
            catch
            {

            }
        }

        private void DataClientes_LostFocus(object sender, RoutedEventArgs e)
        {

        }

        private void DataClientes_SourceUpdated(object sender, DataTransferEventArgs e)
        {

        }

        private void UserControl_FocusableChanged(object sender, DependencyPropertyChangedEventArgs e)
        {

        }

        private void dataClientes_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            butInserir.IsEnabled = true;
            butEditar.IsEnabled = true;
            try
            {
                if (dataClientes.SelectedItem != null)
                {
                    DataRowView v = (DataRowView)dataClientes.SelectedItem;
                    codC = int.Parse(v[0].ToString());
                    nome = v[1].ToString();
                    dataN = v[2].ToString();
                    email = v[3].ToString();
                    contacto = v[4].ToString();
                }
            }
            catch
            {

            }
        }
    }
}
