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
    /// Interação lógica para ProfessoresView.xam
    /// </summary>
    public partial class ProfessoresView : UserControl
    {
        double salario;
        int codP;
        string nome, dataN, contacto;
        int but = 0;
        int verifica = 0;

        public ProfessoresView()
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
                string query = string.Format("SELECT * FROM professores;");
                MySqlDataAdapter da = new MySqlDataAdapter(query, b.con);
                da.Fill(dt);
                gridProfessores.ItemsSource = dt.DefaultView;// Área para fazer algo mais com a grid view...
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
            but = 1;
            BDProfessores c = new BDProfessores(codP, nome, dataN, salario, contacto, but);
            c.Show();
        }

        private void butEditar_Click(object sender, RoutedEventArgs e)
        {
            verifica = 1;
            but = 2;
            BDProfessores c = new BDProfessores(codP, nome, dataN, salario, contacto, but);
            c.Show();
        }

        private void search_TextChanged(object sender, TextChangedEventArgs e)
        {
            BD b = new BD();
            b.LigaBD();
            try
            {
                DataTable dt = new DataTable();
                string query = string.Format("SELECT * FROM professores where nome like '" + search.Text + "%' or contacto like '" + search.Text + "%' or CodP like '" + search.Text + "%';");
                MySqlDataAdapter da = new MySqlDataAdapter(query, b.con);
                da.Fill(dt);
                gridProfessores.ItemsSource = dt.DefaultView;// Área para fazer algo mais com a grid view...
            }
            catch
            {
                MessageBox.Show("Não foram retornados dados!", "ERRO");
            }
            b.FechaBD();
        }

        private void UserControl_MouseMove(object sender, MouseEventArgs e)
        {
            try
            {
                if (Application.Current.Windows.OfType<BDProfessores>().Any())
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

        private void gridProfessores_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            butInserir.IsEnabled = true;
            butEditar.IsEnabled = true;
            try
            {
                if (gridProfessores.SelectedItem != null)
                {
                    DataRowView v = (DataRowView)gridProfessores.SelectedItem;
                    codP = int.Parse(v[0].ToString());
                    nome = v[1].ToString();
                    dataN = v[2].ToString();
                    salario = double.Parse(v[4].ToString());
                    contacto = v[3].ToString();
                }
            }
            catch
            {

            }
        }

        private void gridProfessores_AutoGeneratingColumn(object sender, DataGridAutoGeneratingColumnEventArgs e)
        {
            if (e.PropertyName == "dataNascimento")// Atenção ao campo da datagrid! Neste exemplo chama-se dataF.
            {
                DataGridTextColumn coluna = e.Column as DataGridTextColumn;
                Binding binding = coluna.Binding as Binding;
                binding.StringFormat = "yyyy-MM-dd";

            }
        }
    }
}
