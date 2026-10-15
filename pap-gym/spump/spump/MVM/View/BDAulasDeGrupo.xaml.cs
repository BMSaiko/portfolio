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
    /// Interaction logic for BDAulasDeGrupo.xaml
    /// </summary>
    public partial class BDAulasDeGrupo : Window
    {
        string Hora, Data, Sala, Professor, Modalidade;
        int But, CodA, CodS, CodP, CodM; 
        string dataA;

        string[] salas = new string[4];

        private void horaCombo_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            int posicao;
            string hora;
            string dataA = dataTxt.SelectedDate.Value.ToString("yyyy-MM-dd");

            try
            {
                salaCombo.Items.Clear();
                posicao = horaCombo.SelectedItem.ToString().IndexOf(":");
                //MessageBox.Show(posicao.ToString());
                hora = horaCombo.SelectedItem.ToString();
                //MessageBox.Show(hora.ToString());
                BD b = new BD();
                b.LigaBD();
                MySqlDataReader reader = null;
                string query = string.Format("SELECT s.NumeroSala, s.limite from salas s where s.NumeroSala NOT IN( SELECT s.NumeroSala from salas s, aulagrupo a where s.NumeroSala=a.NumeroSala and datas='{0}' and hora='{1}')", dataA, hora);
                //MessageBox.Show(query);
                MySqlCommand cmd = new MySqlCommand(query, b.con);
                reader = cmd.ExecuteReader();

                while (reader.Read())
                {
                    //MessageBox.Show(reader[0].ToString());
                    //MessageBox.Show(reader[1].ToString());
                    CarregaComboSala(reader[0].ToString());
                    //MessageBox.Show(Sala);
                }
                b.FechaBD();
            }
            catch
            {

            }
        }

        public BDAulasDeGrupo(int codA, string datas, string hora, int numeroSala, int codP, int codM, int buT)
        {
            InitializeComponent();


            CarregaComboProfessor();
            CarregaComboModalidades();



            CodA = codA;
            Data = datas;
            Hora = hora;
            CodS = numeroSala;
            CodP = codP;
            CodM = codM;
            But = buT;





            if (But == 2)
            {
                Sala = string.Format("SELECT nome from salas where NumeroSala='{0}'", CodS);

                BD b = new BD();
                b.LigaBD();
                MySqlDataReader reader = null;

                MySqlCommand cmd = new MySqlCommand(Sala, b.con);
                reader = cmd.ExecuteReader();

                while (reader.Read())
                {
                    Sala = reader[0].ToString();
                    //MessageBox.Show(Sala);
                }
                b.FechaBD();

                Professor = string.Format("SELECT nome from professores where CodP='{0}'", CodP);

                b.LigaBD();

                MySqlCommand cmd2 = new MySqlCommand(Professor, b.con);
                reader = cmd2.ExecuteReader();

                while (reader.Read())
                {
                    Professor = reader[0].ToString();
                    //MessageBox.Show(Professor);
                }
                b.FechaBD();

                Modalidade = string.Format("SELECT nome from modalidades where CodM='{0}'", CodM);

                b.LigaBD();

                MySqlCommand cmd3 = new MySqlCommand(Modalidade, b.con);
                reader = cmd3.ExecuteReader();

                while (reader.Read())
                {
                    Modalidade = reader[0].ToString();
                    //MessageBox.Show(Modalidade);
                }
                b.FechaBD();

                dataTxt.Text = Data;
                horaCombo.Text = Hora;
                salaCombo.Text = Sala;
                professorCombo.Text = Professor;
                modalidadeCombo.Text = Modalidade;
            }
        }

        private void DataTxt_SelectedDateChanged(object sender, SelectionChangedEventArgs e)
        {
            BD b = new BD();
            dataA = dataTxt.SelectedDate.Value.ToString("yyyy-MM-dd");
            string query = string.Format("SELECT count(hora), hora from aulagrupo where datas='{0}' group by hora", dataA);
            //t1.Text = query;
            List<int> listaHoras = new List<int>();
            int numOcupadas = 0;
            int i = 0, full = 0;
            int index = 0;

            try
            {

                b.LigaBD();
                MySqlDataReader reader1 = null;
                try
                {
                    string query1 = "SELECT nome FROM salas";
                    // ou string query = string.Format("select marca, modelo from carros where marca='{0}' and pais='{1}'", textmarca.Text, TextPais.Text);
                    //MessageBox.Show(query1);
                    MySqlCommand cmd1 = new MySqlCommand(query1, b.con);
                    reader1 = cmd1.ExecuteReader(); //executar o reader
                    while (reader1.Read())
                    {
                        //MessageBox.Show(reader1[0].ToString());
                        index++;
                    }
                    //MessageBox.Show(index.ToString());
                }
                catch
                {
                    MessageBox.Show("Dados Inválidos!");
                }
                b.FechaBD();

                horaCombo.Items.Clear();
                horaCombo.SelectedIndex = -1;


                b.LigaBD();
                MySqlDataReader reader = null;

                MySqlCommand cmd = new MySqlCommand(query, b.con);
                reader = cmd.ExecuteReader();
                while (reader.Read())
                {
                    numOcupadas = Convert.ToInt32(reader[0].ToString());
                    if (numOcupadas == index)
                    {
                        listaHoras.Add(int.Parse(reader[1].ToString().Substring(0, 2)));
                    }
                }

                for (i = 10; i < 21; i++)
                {
                    full = 0;
                    foreach (int h in listaHoras)
                    {
                        if (i == h)
                        {
                            full = 1;
                        }
                    }
                    if (full == 0)
                    {
                        horaCombo.Items.Add(i.ToString() + ":00");
                    }
                }

            }
            catch
            {

            }
        }

        private void closeWindow_Click(object sender, RoutedEventArgs e)
        {
            this.Close();
        }

        private void butConfirm_Click(object sender, RoutedEventArgs e)
        {
            Data = dataA;
            Hora = horaCombo.Text;
            Sala = salaCombo.Text;
            Professor = professorCombo.Text;
            Modalidade = modalidadeCombo.Text;


            string query1 = string.Format("SELECT NumeroSala from salas where nome='{0}'", Sala);

            BD b = new BD();
            b.LigaBD();
            MySqlDataReader reader = null;

            MySqlCommand cmd = new MySqlCommand(query1, b.con);
            reader = cmd.ExecuteReader();

            while (reader.Read())
            {
                //MessageBox.Show(reader[0].ToString());
                CodS = int.Parse(reader[0].ToString());
                //MessageBox.Show(Sala);
            }
            b.FechaBD();

            string query2 = string.Format("SELECT CodP from professores where nome='{0}'", Professor);

            b.LigaBD();

            MySqlCommand cmd2 = new MySqlCommand(query2, b.con);
            reader = cmd2.ExecuteReader();

            while (reader.Read())
            {
                CodP = int.Parse(reader[0].ToString());
                //MessageBox.Show(Professor);
            }
            b.FechaBD();

            string query3 = string.Format("SELECT CodM from modalidades where nome='{0}'", Modalidade);

            b.LigaBD();

            MySqlCommand cmd3 = new MySqlCommand(query3, b.con);
            reader = cmd3.ExecuteReader();

            while (reader.Read())
            {
                CodM = int.Parse(reader[0].ToString());
                //MessageBox.Show(Modalidade);
            }
            b.FechaBD();




            if (But == 1)
            {
                try
                {
                    b.LigaBD();
                    string query = string.Format("insert into aulagrupo values (null,'{0}','{1}',{2},{3},{4});", Data, Hora, CodS, CodP, CodM); // o número não leva pelicas
                    //MessageBox.Show(query);
                    MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                    comandoMySQL.ExecuteNonQuery();
                    MessageBox.Show("Aula inserida com sucesso.");
                    b.FechaBD();
                }
                catch
                {
                    MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Aulas de Grupo");
                }
                this.Close();
            }
            else if(But == 2)
            {
                try
                {
                    b.LigaBD();
                    string query = string.Format("UPDATE aulagrupo set datas= '{0}', hora= '{1}', NumeroSala= {2}, CodP= {3}, CodM= {4} where CodAula = {5}", Data, Hora, CodS, CodP, CodM, CodA); // o número não leva pelicas
                    //MessageBox.Show(query);
                    MySqlCommand comandoMySQL = new MySqlCommand(query, b.con); //query SQL e conexão como parâmetros
                    comandoMySQL.ExecuteNonQuery();
                    MessageBox.Show("Aula editada com sucesso.");
                    b.FechaBD();
                }
                catch
                {
                    MessageBox.Show("Dados Inválidos!\nAltere os valores.", "Aulas de Grupo");
                }
                this.Close();
            }
        }

        private void CarregaComboSala(string c)
        {
            BD b = new BD();
            b.LigaBD();
            MySqlDataReader reader = null;

            salaCombo.IsEditable = false; //Evitar edições não autorizadas na combo box
            try
            {
                String query = string.Format(@"SELECT nome from Salas where NumeroSala={0};", int.Parse(c.ToString()));
                MySqlCommand cmd = new MySqlCommand(query, b.con);
                reader = cmd.ExecuteReader(); //executa o reader
                while (reader.Read())
                {
                    //MessageBox.Show(reader[0].ToString());
                    salaCombo.Items.Add(reader[0]); //Cada item pode ser composto por um ou mais campos 
                }
            }
            catch
            {
                MessageBox.Show("Algo não correu bem!");
            }
            b.FechaBD();
        }  

        private void CarregaComboProfessor()
        {
            BD b = new BD();
            b.LigaBD();
            MySqlDataReader reader = null;
            professorCombo.Items.Clear();
            professorCombo.IsEditable = false; //Evitar edições não autorizadas na combo box
            try
            {
                String query = @"SELECT * from professores;";
                MySqlCommand cmd = new MySqlCommand(query, b.con);
                reader = cmd.ExecuteReader(); //executa o reader
                while (reader.Read())
                {
                    professorCombo.Items.Add(reader[1]); //Cada item pode ser composto por um ou mais campos 
                }
            }
            catch
            {
                MessageBox.Show("Algo não correu bem!");
            }
            b.FechaBD();
        }

        private void CarregaComboModalidades()
        {
            BD b = new BD();
            b.LigaBD();
            MySqlDataReader reader = null;
            modalidadeCombo.Items.Clear();
            modalidadeCombo.IsEditable = false; //Evitar edições não autorizadas na combo box
            try
            {
                String query = @"SELECT * from modalidades;";
                MySqlCommand cmd = new MySqlCommand(query, b.con);
                reader = cmd.ExecuteReader(); //executa o reader
                while (reader.Read())
                {
                    modalidadeCombo.Items.Add(reader[1]); //Cada item pode ser composto por um ou mais campos 
                }
            }
            catch
            {
                MessageBox.Show("Algo não correu bem!");
            }
            b.FechaBD();
        }
    }
}
