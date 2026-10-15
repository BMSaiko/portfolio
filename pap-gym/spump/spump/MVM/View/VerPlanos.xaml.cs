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
    /// Interaction logic for VerPlanos.xaml
    /// </summary>
    public partial class VerPlanos : Window
    {
        string nomeTreino = "";

        public VerPlanos()
        {
            InitializeComponent();
            I1.IsEnabled = false;
            I2.IsEnabled = false;
            I3.IsEnabled = false;
            I4.IsEnabled = false;
            I5.IsEnabled = false;
            I6.IsEnabled = false;
            I7.IsEnabled = false;


            int altura = 0;
            int i = 0;
            Label[] a = new Label[30];


            BD b = new BD();
            b.LigaBD();
            MySqlDataReader reader = null;
            try
            {
                string query = "SELECT DISTINCT nome from planos";
                MySqlCommand cmd = new MySqlCommand(query, b.con);
                reader = cmd.ExecuteReader();

                while (reader.Read())
                {
                    //Grid.Height = Grid.Height + 50;
                    a[i] = new Label();

                    a[i].Name = "Plano" + i;
                    a[i].Content = reader[0].ToString();

                    a[i].Width = 200;
                    a[i].Height = 50;

                    a[i].Margin = new Thickness(50, altura, 0, 0);
                    //altura = altura + 5;

                    a[i].HorizontalAlignment = HorizontalAlignment.Left;
                    a[i].VerticalAlignment = VerticalAlignment.Top;
                    a[i].VerticalContentAlignment = VerticalAlignment.Center;

                    a[i].FontSize = 14;

                    a[i].Foreground = Brushes.White;

                    //BrushConverter bc = new BrushConverter();
                    //Brush brush = (Brush)bc.ConvertFrom("#FF3D3D3D");
                    //brush.Freeze();
                    //a[i].Background = brush;

                    a[i].MouseDown += LabelClick;
                    //a[i].MouseEnter += Enter;
                    //a[i].MouseLeave += Leave;

                    Grid.Children.Add(a[i]);

                    i++;
                }
            }
            catch
            {
                MessageBox.Show("Dados Invalidos 1!");
            }
            b.FechaBD();
        }

        public void LabelClick(object sender, MouseButtonEventArgs e)
        {
            var teste = sender as Label;
            nomeTreino = teste.Content.ToString();

            //List<string> listaNomes = new List<string>();
            int i = 0;

            BD b = new BD();
            b.LigaBD();
            MySqlDataReader reader = null;
            try
            {
                string query = "SELECT e.nome FROM planos p, exercicios e WHERE p.nome ='"+ nomeTreino +"' and e.CodEx = p.CodEx";
                // ou string query = string.Format("select marca, modelo from carros where marca='{0}' and pais='{1}'", textmarca.Text, TextPais.Text);
                //MessageBox.Show(query);
                MySqlCommand cmd = new MySqlCommand(query, b.con);
                reader = cmd.ExecuteReader(); //executar o reader
                while (reader.Read())
                {
                    //listaNomes.Add(reader.GetString(0));
                    //MessageBox.Show(reader[0].ToString());

                    if(i == 0)
                    {
                        I1.Content = reader[0].ToString();
                        I1.Foreground= Brushes.Transparent;
                        l1.Content = reader[0].ToString();
                        var img = new ImageBrush();
                        img.ImageSource = new BitmapImage(new Uri(@"./IMG/" + reader[0] + ".jpg", UriKind.RelativeOrAbsolute));
                        I1.Background = img;

                        //I1.Source = new BitmapImage(new Uri(@"../../Images/" + reader[0].ToString() + "", UriKind.RelativeOrAbsolute));
                    }
                    else if (i == 1)
                    {
                        I2.Content = reader[0].ToString();
                        I2.Foreground = Brushes.Transparent;
                        l2.Content = reader[0].ToString();
                        var img = new ImageBrush();
                        img.ImageSource = new BitmapImage(new Uri(@"./IMG/" + reader[0] + ".jpg", UriKind.RelativeOrAbsolute));
                        I2.Background = img;
                    }
                    else if (i == 2)
                    {
                        I3.Content = reader[0].ToString();
                        I3.Foreground = Brushes.Transparent;
                        l3.Content = reader[0].ToString();
                        var img = new ImageBrush();
                        img.ImageSource = new BitmapImage(new Uri(@"./IMG/" + reader[0] + ".jpg", UriKind.RelativeOrAbsolute));
                        I3.Background = img;
                    }
                    else if (i == 3)
                    {
                        I4.Content = reader[0].ToString();
                        I4.Foreground = Brushes.Transparent;
                        l4.Content = reader[0].ToString();
                        var img = new ImageBrush();
                        img.ImageSource = new BitmapImage(new Uri(@"./IMG/" + reader[0] + ".jpg", UriKind.RelativeOrAbsolute));
                        I4.Background = img;
                    }
                    else if (i == 4)
                    {
                        I5.Content = reader[0].ToString();
                        I5.Foreground = Brushes.Transparent;
                        l5.Content = reader[0].ToString();
                        var img = new ImageBrush();
                        img.ImageSource = new BitmapImage(new Uri(@"./IMG/" + reader[0] + ".jpg", UriKind.RelativeOrAbsolute));
                        I5.Background = img;
                    }
                    else if (i == 5)
                    {
                        I6.Content = reader[0].ToString();
                        I6.Foreground = Brushes.Transparent;
                        l6.Content = reader[0].ToString();
                        var img = new ImageBrush();
                        img.ImageSource = new BitmapImage(new Uri(@"./IMG/" + reader[0] + ".jpg", UriKind.RelativeOrAbsolute));
                        I6.Background = img;
                    }
                    else
                    {
                        I7.Content = reader[0].ToString();
                        I7.Foreground = Brushes.Transparent;
                        l7.Content = reader[0].ToString();
                        var img = new ImageBrush();
                        img.ImageSource = new BitmapImage(new Uri(@"./IMG/" + reader[0] + ".jpg", UriKind.RelativeOrAbsolute));
                        I7.Background = img;
                    }

                    i++;
                }

                I1.IsEnabled = true;
                I2.IsEnabled = true;
                I3.IsEnabled = true;
                I4.IsEnabled = true;
                I5.IsEnabled = true;
                I6.IsEnabled = true;
                I7.IsEnabled = true;
            }
            catch
            {
                MessageBox.Show("Dados Inválidos!");
            }
            b.FechaBD();
        }

        private void CloseMainwindow_Click(object sender, RoutedEventArgs e)
        {
            this.Close();
        }

        private void I1_MouseDown(object sender, MouseButtonEventArgs e)
        {
            //MessageBox.Show(I1.Content.ToString());
            VerExercicios c = new VerExercicios(I1.Content.ToString(), nomeTreino);
            c.Show();
        }

        private void I2_MouseDown(object sender, MouseButtonEventArgs e)
        {
            //MessageBox.Show(I2.Content.ToString());
            VerExercicios c = new VerExercicios(I2.Content.ToString(), nomeTreino);
            c.Show();
        }

        private void I3_MouseDown(object sender, MouseButtonEventArgs e)
        {
            //MessageBox.Show(I3.Content.ToString());
            VerExercicios c = new VerExercicios(I3.Content.ToString(), nomeTreino);
            c.Show();
        }

        private void I4_MouseDown(object sender, MouseButtonEventArgs e)
        {
            //MessageBox.Show(I4.Content.ToString());
            VerExercicios c = new VerExercicios(I4.Content.ToString(), nomeTreino);
            c.Show();
        }

        private void I5_MouseDown(object sender, MouseButtonEventArgs e)
        {
            //MessageBox.Show(I5.Content.ToString());
            VerExercicios c = new VerExercicios(I5.Content.ToString(), nomeTreino);
            c.Show();
        }

        private void I6_MouseDown(object sender, MouseButtonEventArgs e)
        {
            //MessageBox.Show(I6.Content.ToString());
            VerExercicios c = new VerExercicios(I6.Content.ToString(), nomeTreino);
            c.Show();
        }

        private void I7_MouseDown(object sender, MouseButtonEventArgs e)
        {
            //MessageBox.Show(I7.Content.ToString());
            VerExercicios c = new VerExercicios(I7.Content.ToString(), nomeTreino);
            c.Show();
        }
    }
}
