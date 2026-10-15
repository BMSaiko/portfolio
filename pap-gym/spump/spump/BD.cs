using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace spump
{
    internal class BD
    {
        public string str;
        public MySqlConnection con;

        public BD()
        {
            str = @"server=localhost;database=gym;userid=root;password=" + Environment.GetEnvironmentVariable("GYM_DB_PASSWORD") + ";SslMode=none";
            con = null;
        }

        public void LigaBD()
        {
            try
            {
                con = new MySqlConnection(str); // con e str foram definidos como variáveis de classe
                con.Open(); //abrir a conexão //MessageBox.Show("Ligou");
            }
            catch (MySqlException err)
            {
                MessageBox.Show("Erro: " + err.ToString());
            }
        }

        public void FechaBD()
        {
            if (con != null)
            {
                con.Close();
            }
        }
    }
}
