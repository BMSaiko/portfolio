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
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace spump.MVM.View
{
    /// <summary>
    /// Interação lógica para HomeView.xam
    /// </summary>
    public partial class HomeView : UserControl
    {
        public HomeView()
        {
            InitializeComponent();
        }

        private void Border_MouseDown(object sender, MouseButtonEventArgs e)
        {
            VerPlanos c = new VerPlanos();
            c.Show();
        }

        private void Border_MouseDown_1(object sender, MouseButtonEventArgs e)
        {

        }

        private void Border_MouseDown_2(object sender, MouseButtonEventArgs e)
        {
            VerAulas c = new VerAulas();
            c.Show();
        }

        private void Border_MouseDown_3(object sender, MouseButtonEventArgs e)
        {
            Info c = new Info();
            c.Show();
        }

        private void Image_MouseDown(object sender, MouseButtonEventArgs e)
        {

        }

        private void Border_MouseDown_4(object sender, MouseButtonEventArgs e)
        {
            BDReservas c = new BDReservas();
            c.Show();
        }
    }
}
