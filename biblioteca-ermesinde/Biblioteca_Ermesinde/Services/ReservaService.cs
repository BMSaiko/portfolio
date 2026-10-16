// ReservaService.cs
using System;
using System.Collections.Generic;
using Biblioteca_Ermesinde.Models;

namespace Biblioteca_Ermesinde.Services
{
    public interface IReservaService
    {
        List<Reserva> GetReservas();
        void AddReserva(Reserva reserva);
        object FirstOrDefault(Func<object, bool> value);
    }

    public class ReservaService : IReservaService
    {
        private readonly List<Reserva> _reservas = new List<Reserva>();

        public List<Reserva> GetReservas()
        {
            return _reservas;
        }

        public void AddReserva(Reserva reserva)
        {
            _reservas.Add(reserva);
        }

        public object FirstOrDefault(Func<object, bool> value)
        {
            throw new NotImplementedException();
        }
    }
}
