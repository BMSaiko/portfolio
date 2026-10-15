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
    /// Interaction logic for VerExercicios.xaml
    /// </summary>
    public partial class VerExercicios : Window
    {
        string Nome, NomePlano;
        public VerExercicios()
        {
            InitializeComponent();
        }

        public VerExercicios(string nome, string nomePlano)
        {
            InitializeComponent();
            Nome = nome;
            NomePlano = nomePlano;
            //MessageBox.Show(Nome);
            int i = 0;
            BD b = new BD();
            b.LigaBD();
            MySqlDataReader reader = null;
            try
            {

                string query = "SELECT e.* FROM planos p, exercicios e WHERE p.nome ='" + NomePlano + "' and e.CodEx = p.CodEx and e.nome = '" + Nome + "'";
                // ou string query = string.Format("select marca, modelo from carros where marca='{0}' and pais='{1}'", textmarca.Text, TextPais.Text);
                //MessageBox.Show(query);
                MySqlCommand cmd = new MySqlCommand(query, b.con);
                reader = cmd.ExecuteReader(); //executar o reader
                while (reader.Read())
                {
                    t1.Text = reader[1].ToString();
                    t2.Text = reader[2].ToString();
                    t3.Text = reader[3].ToString();
                    t4.Text = reader[4].ToString();

                    var img = new ImageBrush();
                    img.ImageSource = new BitmapImage(new Uri(@"./IMG/" + reader[1] + ".jpg", UriKind.RelativeOrAbsolute));
                    I.Background = img;

                    i++;
                    //MessageBox.Show(i.ToString());
                }
            }
            catch
            {
                MessageBox.Show("Dados Inválidos!1");
            }
            b.FechaBD();
        }

        private void CloseMainwindow_Click(object sender, RoutedEventArgs e)
        {
            this.Close();
        }
    }
}
