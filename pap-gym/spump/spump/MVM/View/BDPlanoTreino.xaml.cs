using MySql.Data.MySqlClient;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;

namespace spump.MVM.View
{
    /// <summary>
    /// Lógica interna para BDPlanoTreino.xaml
    /// </summary>
    public partial class BDPlanoTreino : Window
    {
        string nome="", nomeA="";
        int numero = 0, But = 0;
        int contador = 0;

        string[] exerDesc = new string[7];
        int[] exerID = new int[7];

        public BDPlanoTreino()
        {
            InitializeComponent();
            CarregaDadosGrid();
            contador = 0;
        }

        public BDPlanoTreino(string Nome, int buT)
        {
            InitializeComponent();
            CarregaDadosGrid();

            But = buT;

            if (But == 2)
            {
                nomeA = Nome;
                planoTxt.Text = Nome;
                int c = 0;

                N1.IsEnabled = false;
                N2.IsEnabled = false;
                N3.IsEnabled = false;
                N4.IsEnabled = false;
                N5.IsEnabled = false;
                N6.IsEnabled = false;
                N7.IsEnabled = false;

                BD b = new BD();
                MySqlDataReader reader = null;

                b.LigaBD();
                string query = "SELECT distinct e.Nome FROM exercicios e, planos p WHERE p.nome='" + planoTxt.Text + "' and p.CodEx = e.CodEx";
                MySqlCommand cmd = new MySqlCommand(query, b.con);
                reader = cmd.ExecuteReader(); //executar o reader
                while (reader.Read())
                {
                    //todo: clear this line
                    //MessageBox.Show(reader[0].ToString());
                    //Debug.WriteLine("exerID--> " + reader[0].ToString());

                    //exerDesc[c] = reader[0].ToString();

                    //MessageBox.Show(reader[0].ToString());

                    if (c == 0)
                    {
                        N1.Text = reader[0].ToString();
                    }
                    else if (c == 1)
                    {
                        N2.Text = reader[0].ToString();
                    }
                    else if (c == 2)
                    {
                        N3.Text = reader[0].ToString();
                    }
                    else if (c == 3)
                    {
                        N4.Text = reader[0].ToString();
                    }
                    else if (c == 4)
                    {
                        N5.Text = reader[0].ToString();
                    }
                    else if (c == 5)
                    {
                        N6.Text = reader[0].ToString();
                    }
                    else if (c == 6)
                    {
                        N7.Text = reader[0].ToString();
                    }
                    c++;
                }
                b.FechaBD();
                
            }
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
                exercicios.ItemsSource = dt.DefaultView;// Área para fazer algo mais com a grid view...
            }
            catch
            {
                MessageBox.Show("Não foram retornados dados!", "ERRO");
            }
            b.FechaBD();
        }

        private void N1_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if(contador == 0)
            {
                contador++;
            }
            else
            {
                //contador--;
                //contador++;
            }

            numero = 1;
            N1.Foreground = Brushes.Red;
            N2.Foreground = Brushes.White;
            N3.Foreground = Brushes.White;
            N4.Foreground = Brushes.White;
            N5.Foreground = Brushes.White;
            N6.Foreground = Brushes.White;
            N7.Foreground = Brushes.White;
            //MessageBox.Show(numero.ToString());

        }

        private void N2_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (contador == 0)
            {
                contador++;
            }
            else
            {
                //contador--;
                //contador++;
            }
            numero = 2;
            N2.Foreground = Brushes.Red;
            N1.Foreground = Brushes.White;
            N3.Foreground = Brushes.White;
            N4.Foreground = Brushes.White;
            N5.Foreground = Brushes.White;
            N6.Foreground = Brushes.White;
            N7.Foreground = Brushes.White;
            //MessageBox.Show(numero.ToString());
        }

        private void N3_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (contador == 0)
            {
                contador++;
            }
            else
            {
                //contador--;
                //contador++;
            }
            numero = 3;
            N3.Foreground = Brushes.Red;
            N1.Foreground = Brushes.White;
            N2.Foreground = Brushes.White;
            N4.Foreground = Brushes.White;
            N5.Foreground = Brushes.White;
            N6.Foreground = Brushes.White;
            N7.Foreground = Brushes.White;
            //MessageBox.Show(numero.ToString());
        }

        private void N4_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (contador == 0)
            {
                contador++;
            }
            else
            {
                //contador--;
                //contador++;
            }
            numero = 4;
            N4.Foreground = Brushes.Red;
            N1.Foreground = Brushes.White;
            N2.Foreground = Brushes.White;
            N3.Foreground = Brushes.White;
            N5.Foreground = Brushes.White;
            N6.Foreground = Brushes.White;
            N7.Foreground = Brushes.White;
            //MessageBox.Show(numero.ToString());
        }

        private void N5_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (contador == 0)
            {
                contador++;
            }
            else
            {
                //contador--;
                //contador++;
            }
            numero = 5;
            N5.Foreground = Brushes.Red;
            N1.Foreground = Brushes.White;
            N2.Foreground = Brushes.White;
            N3.Foreground = Brushes.White;
            N4.Foreground = Brushes.White;
            N6.Foreground = Brushes.White;
            N7.Foreground = Brushes.White;
            //MessageBox.Show(numero.ToString());
        }

        private void N6_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (contador == 0)
            {
                contador++;
            }
            else
            {
                //contador--;
                //contador++;
            }
            numero = 6;
            N6.Foreground = Brushes.Red;
            N1.Foreground = Brushes.White;
            N2.Foreground = Brushes.White;
            N3.Foreground = Brushes.White;
            N4.Foreground = Brushes.White;
            N5.Foreground = Brushes.White;
            N7.Foreground = Brushes.White;
            //MessageBox.Show(numero.ToString());
        }

        private void N7_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (contador == 0)
            {
                contador++;
            }
            else
            {
                //contador--;
                //contador++;
            }
            numero = 7;
            N7.Foreground = Brushes.Red; 
            N1.Foreground = Brushes.White;
            N2.Foreground = Brushes.White;
            N3.Foreground = Brushes.White;
            N4.Foreground = Brushes.White;
            N5.Foreground = Brushes.White;
            N6.Foreground = Brushes.White;
            //MessageBox.Show(numero.ToString());
        }

        private void exercicios_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            try
            {
                if (exercicios.SelectedItem != null)
                {
                    DataRowView v = (DataRowView)exercicios.SelectedItem;
                    nome = v[1].ToString();
                }
            }
            catch
            {

            }
        }

        private void exercicios_SelectedCellsChanged(object sender, SelectedCellsChangedEventArgs e)
        {
            if (numero == 1)
            {
                N1.Text = nome;
                N1.Foreground = Brushes.White;
                N2.Foreground = Brushes.White;
                N3.Foreground = Brushes.White;
                N4.Foreground = Brushes.White;
                N5.Foreground = Brushes.White;
                N6.Foreground = Brushes.White;
                N7.Foreground = Brushes.White;
            }
            else if (numero == 2)
            {
                N2.Text = nome;
                N1.Foreground = Brushes.White;
                N2.Foreground = Brushes.White;
                N3.Foreground = Brushes.White;
                N4.Foreground = Brushes.White;
                N5.Foreground = Brushes.White;
                N6.Foreground = Brushes.White;
                N7.Foreground = Brushes.White;
            }
            else if (numero == 3)
            {
                N3.Text = nome;
                N1.Foreground = Brushes.White;
                N2.Foreground = Brushes.White;
                N3.Foreground = Brushes.White;
                N4.Foreground = Brushes.White;
                N5.Foreground = Brushes.White;
                N6.Foreground = Brushes.White;
                N7.Foreground = Brushes.White;
            }
            else if (numero == 4)
            {
                N4.Text = nome;
                N1.Foreground = Brushes.White;
                N2.Foreground = Brushes.White;
                N3.Foreground = Brushes.White;
                N4.Foreground = Brushes.White;
                N5.Foreground = Brushes.White;
                N6.Foreground = Brushes.White;
                N7.Foreground = Brushes.White;
            }
            else if (numero == 5)
            {
                N5.Text = nome;
                N1.Foreground = Brushes.White;
                N2.Foreground = Brushes.White;
                N3.Foreground = Brushes.White;
                N4.Foreground = Brushes.White;
                N5.Foreground = Brushes.White;
                N6.Foreground = Brushes.White;
                N7.Foreground = Brushes.White;
            }
            else if (numero == 6)
            {
                N6.Text = nome;
                N1.Foreground = Brushes.White;
                N2.Foreground = Brushes.White;
                N3.Foreground = Brushes.White;
                N4.Foreground = Brushes.White;
                N5.Foreground = Brushes.White;
                N6.Foreground = Brushes.White;
                N7.Foreground = Brushes.White;
            }
            else if (numero == 7)
            {
                N7.Text = nome;
                N1.Foreground = Brushes.White;
                N2.Foreground = Brushes.White;
                N3.Foreground = Brushes.White;
                N4.Foreground = Brushes.White;
                N5.Foreground = Brushes.White;
                N6.Foreground = Brushes.White;
                N7.Foreground = Brushes.White;
            }
            else
            {
                MessageBox.Show("Escolha um campo para poder escolher um exercicio!");
            }
        }


        private void Button_Click(object sender, RoutedEventArgs e)
        {
            if(N1.Text != "-" && N2.Text != "-" && N3.Text != "-" && N4.Text != "-" && N5.Text != "-" && N6.Text != "-" && N7.Text != "-" && planoTxt.Text != "")
            {
                BD b = new BD();


                int index = 0;
                foreach (var exerD in StackExer.Children.OfType<TextBlock>())
                {
                    //Debug.WriteLine("exerDesc --> " + exerD.Text);
                    exerDesc[index] = exerD.Text;
                    //MessageBox.Show(exerDesc[index]);
                    index++;
                }


                MySqlDataReader reader = null;
                //Debug.WriteLine("exerDesc --> " + exerDesc[0]);
                index = 0;

                foreach (var desc in exerDesc)
                {
                    b.LigaBD();
                    string query = "SELECT CodEx FROM exercicios WHERE nome='" + desc + "'";
                    MySqlCommand cmd = new MySqlCommand(query, b.con);
                    reader = cmd.ExecuteReader(); //executar o reader
                    while (reader.Read())
                    {
                        //todo: clear this line
                        //MessageBox.Show(reader[0].ToString());
                        //Debug.WriteLine("exerID--> " + reader[0].ToString());

                        exerID[index] = int.Parse(reader[0].ToString());
                        index++;
                    }
                    b.FechaBD();
                }

                if (But == 1)
                {
                    nome = planoTxt.Text;
                    try
                    {
                        foreach (var id in exerID)
                        {
                            b.LigaBD();
                            string query = string.Format("insert into planos values (null,{0},{1},'{2}');", id, 7, nome);
                            //MessageBox.Show(query);
                            MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                            comandoMySQL.ExecuteNonQuery();

                            b.FechaBD();
                        }


                        MessageBox.Show("Plano inserido com sucesso.", "Planos de Treino");

                    }
                    catch
                    {
                        MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Planos de Treino");
                    }
                    this.Close();
                }
                else if (But == 2)
                {
                    nome = planoTxt.Text;
                    try
                    {
                        foreach (var id in exerID)
                        {
                            b.LigaBD();
                            //MessageBox.Show(id.ToString());
                            string query = string.Format("UPDATE planos SET nome='{0}' where nome='{1}'", nome, nomeA);
                            //MessageBox.Show(query);
                            MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                            comandoMySQL.ExecuteNonQuery();

                            b.FechaBD();
                        }


                        MessageBox.Show("Plano inserido com sucesso.", "Planos");

                    }
                    catch
                    {
                        MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Planos");
                    }
                    this.Close();
                }
            }
            else
            {
                MessageBox.Show("Escolha todos os campo para poder inserir o plano!", "Planos");
            }

            
            
            
        }

        private void CloseMainwindow_Click(object sender, RoutedEventArgs e)
        {
            this.Close();
        }


    }
}
