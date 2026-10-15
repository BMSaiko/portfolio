using spump.Core;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace spump.MVM.ViewModel
{
    internal class MainViewModel : ObeservableObject
    {
        public RelayCommand HomeViewCommand { get; set; }
        public RelayCommand ClientesViewCommand { get; set; }
        public RelayCommand ProfessoresViewCommand { get; set; }
        public RelayCommand AulasGrupoViewCommand { get; set; }
        public RelayCommand ExerciciosViewCommand { get; set; }
        public RelayCommand ModalidadesViewCommand { get; set; }
        public RelayCommand PlanoTreinoViewCommand { get; set; }
        public RelayCommand SalasViewCommand { get; set; }

        public HomeViewModel HomeVM { get; set; }
        public ClientesViewModel ClientesVM { get; set; }
        public ProfessoresViewModel ProfessoresVM { get; set; }
        public AulasGrupoViewModel AulasGrupoVM { get; set; }
        public ExerciciosViewModel ExerciciosVM { get; set; }
        public ModalidadesViewModel ModalidadesVM { get; set; }
        public PlanoTreinoViewModel PlanoTreinoVM { get; set; }
        public SalasViewModel SalasVM { get; set; }

        private object _currentView;

        public object CurrentView
        {
            get { return _currentView; }
            set { _currentView = value;
                  Onpropertychanged();
            }
        }

        public MainViewModel()
        {
            HomeVM = new HomeViewModel();
            ClientesVM = new ClientesViewModel();
            ProfessoresVM = new ProfessoresViewModel();
            AulasGrupoVM = new AulasGrupoViewModel();
            ExerciciosVM = new ExerciciosViewModel();
            ModalidadesVM = new ModalidadesViewModel();
            PlanoTreinoVM = new PlanoTreinoViewModel();
            SalasVM = new SalasViewModel();
            CurrentView = HomeVM;

            HomeViewCommand = new RelayCommand(o =>
            {
                CurrentView = HomeVM;
            });

            ClientesViewCommand = new RelayCommand(o =>
            {
                CurrentView = ClientesVM;
            });

            ProfessoresViewCommand = new RelayCommand(o =>
            {
                CurrentView = ProfessoresVM;
            });

            AulasGrupoViewCommand = new RelayCommand(o =>
            {
                CurrentView = AulasGrupoVM;
            });

            ExerciciosViewCommand = new RelayCommand(o =>
            {
                CurrentView = ExerciciosVM;
            });

            PlanoTreinoViewCommand = new RelayCommand(o =>
            {
                CurrentView = PlanoTreinoVM;
            });

            ModalidadesViewCommand = new RelayCommand(o =>
            {
                CurrentView = ModalidadesVM;
            });

            SalasViewCommand = new RelayCommand(o =>
            {
                CurrentView = SalasVM;
            });
        }
    }
}
