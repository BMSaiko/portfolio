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
    /// Interação lógica para PlanoTreinoView.xam
    /// </summary>
    public partial class PlanoTreinoView : UserControl
    {
        int verifica = 0, BuT = 0;
        string nome;
        public PlanoTreinoView()
        {
            InitializeComponent();
            CarregaDadosGrid();
        }

        private void CarregaDadosGrid()
        {
            BD b = new BD();
            b.LigaBD();
            try
            {
                DataTable dt = new DataTable();
                string query = string.Format("SELECT distinct Nome, quantidade FROM planos order by Nome;");
                MySqlDataAdapter da = new MySqlDataAdapter(query, b.con);
                da.Fill(dt);
                grid.ItemsSource = dt.DefaultView;// Área para fazer algo mais com a grid view...
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
            //MessageBox.Show(verifica.ToString());
            BuT = 1;
            BDPlanoTreino c = new BDPlanoTreino(nome , BuT);
            c.Show();
        }

        private void butEditar_Click(object sender, RoutedEventArgs e)
        {
            verifica = 1;
            //MessageBox.Show(verifica.ToString());
            BuT = 2;
            BDPlanoTreino c = new BDPlanoTreino(nome, BuT);
            c.Show();
        }

        private void search_TextChanged(object sender, TextChangedEventArgs e)
        {
            BD b = new BD();
            b.LigaBD();
            try
            {
                DataTable dt = new DataTable();
                string query = string.Format("SELECT distinct Nome, quantidade FROM planos where nome like '" + search.Text + "%'");
                MySqlDataAdapter da = new MySqlDataAdapter(query, b.con);
                da.Fill(dt);
                grid.ItemsSource = dt.DefaultView;// Área para fazer algo mais com a grid view...
            }
            catch
            {
                MessageBox.Show("Não foram retornados dados!", "ERRO");
            }
            b.FechaBD();
        }
        private void grid_AutoGeneratingColumn(object sender, DataGridAutoGeneratingColumnEventArgs e)
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
                if (Application.Current.Windows.OfType<BDPlanoTreino>().Any())
                {

                }
                else
                {
                    if (verifica == 1)
                    {
                        CarregaDadosGrid();
                        verifica = 0;
                        //MessageBox.Show(verifica.ToString());

                    }
                }
            }
            catch
            {

            }
        }

        private void UserControl_MouseMove_1(object sender, MouseEventArgs e)
        {
            try
            {
                if (Application.Current.Windows.OfType<BDPlanoTreino>().Any())
                {

                }
                else
                {
                    if (verifica == 1)
                    {
                        CarregaDadosGrid();
                        verifica = 0;
                        //MessageBox.Show(verifica.ToString());

                    }
                }
            }
            catch
            {

            }
        }

        private void grid_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            butInserir.IsEnabled = true;
            butEditar.IsEnabled = true;
            try
            {
                if (grid.SelectedItem != null)
                {
                    DataRowView v = (DataRowView)grid.SelectedItem;
                    nome = v[0].ToString();

                }
            }
            catch
            {

            }
        }
    }
}
