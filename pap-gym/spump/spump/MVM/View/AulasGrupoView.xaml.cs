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
    /// Interação lógica para AulasGrupoView.xam
    /// </summary>
    public partial class AulasGrupoView : UserControl
    {
        int codAula, numeroSala, codP, codM, buT = 0, verifica = 0;
        string datas, hora;

        public AulasGrupoView()
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
                string query = string.Format("SELECT * FROM aulagrupo;");
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
            buT = 1;
            verifica = 1;
            BDAulasDeGrupo c = new BDAulasDeGrupo(codAula, datas, hora, numeroSala, codP, codM, buT);
            c.Show();
        }

        private void UserControl_MouseMove(object sender, MouseEventArgs e)
        {
            try
            {
                if (Application.Current.Windows.OfType<BDAulasDeGrupo>().Any())
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

        private void ButReservar_Click(object sender, RoutedEventArgs e)
        {
            buT = 3;
            verifica = 1;
            BDAulasDeGrupo c = new BDAulasDeGrupo(codAula, datas, hora, numeroSala, codP, codM, buT);
            c.Show();
        }

        private void butEditar_Click(object sender, RoutedEventArgs e)
        {
            buT = 2;
            verifica = 1;
            BDAulasDeGrupo c = new BDAulasDeGrupo(codAula, datas, hora, numeroSala, codP, codM, buT);
            c.Show();
        }

        private void search_TextChanged(object sender, TextChangedEventArgs e)
        {
            BD b = new BD();
            b.LigaBD();
            try
            {
                DataTable dt = new DataTable();
                string query = string.Format("SELECT * FROM aulagrupo where datas like '" + search.Text + "%'");
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
            if (e.PropertyName == "datas")// Atenção ao campo da datagrid! Neste exemplo chama-se dataF.
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
                    codAula = int.Parse(v[0].ToString());
                    datas = v[1].ToString();
                    hora = v[2].ToString();
                    numeroSala = int.Parse(v[3].ToString());
                    codP = int.Parse(v[4].ToString());
                    codM = int.Parse(v[5].ToString());
                }
            }
            catch
            {

            }
        }
    }
}
