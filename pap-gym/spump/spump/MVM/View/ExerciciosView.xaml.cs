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
    /// Interação lógica para ExerciciosView.xam
    /// </summary>
    public partial class ExerciciosView : UserControl
    {
        int codEx, series, repeticoes, verifica = 0, buT = 0;
        string nome, descanso;
        public ExerciciosView()
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
                string query = string.Format("SELECT * FROM exercicios;");
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

        private void UserControl_MouseMove(object sender, MouseEventArgs e)
        {
            try
            {
                if (Application.Current.Windows.OfType<BDExercicios>().Any())
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

        private void butInserir_Click(object sender, RoutedEventArgs e)
        {
            verifica = 1;
            buT = 1;
            BDExercicios c = new BDExercicios(codEx, nome, series, repeticoes, descanso, buT);
            c.Show();
        }

        private void butEditar_Click(object sender, RoutedEventArgs e)
        {
            verifica = 1;
            buT = 2;
            BDExercicios c = new BDExercicios(codEx, nome, series, repeticoes, descanso, buT);
            c.Show();
        }

        private void search_TextChanged(object sender, TextChangedEventArgs e)
        {
            BD b = new BD();
            b.LigaBD();
            try
            {
                DataTable dt = new DataTable();
                string query = string.Format("SELECT * FROM exercicios where nome like '" + search.Text + "%'");
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


        private void grid_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            butInserir.IsEnabled = true;
            butEditar.IsEnabled = true;
            try
            {
                if (grid.SelectedItem != null)
                {
                    DataRowView v = (DataRowView)grid.SelectedItem;
                    codEx = int.Parse(v[0].ToString());
                    nome = v[1].ToString();
                    series = int.Parse(v[2].ToString());
                    repeticoes = int.Parse(v[3].ToString());
                    descanso = v[4].ToString();
                }
            }
            catch
            {

            }
        }
    }
}
