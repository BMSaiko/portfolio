using Microsoft.EntityFrameworkCore;

namespace Biblioteca_Ermesinde.Models
{
    public class BackEndContext : DbContext
    {
        public DbSet<Cliente> Clientes { get; set; }
        public DbSet<Livro> Livros { get; set; }
        public DbSet<Reserva> Reservas { get; set; }  // Corrigido o nome para Reservas
        public DbSet<ReservaLivro> ReservasLivros { get; set; }

        public BackEndContext(DbContextOptions<BackEndContext> options) : base(options)
        {
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseSqlServer(@"Server=10.1.23.254;Database=Biblioteca_Ermesinde;User Id=sa;Password=" + Environment.GetEnvironmentVariable("SA_PASSWORD") + ";TrustServerCertificate=True");
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Reserva>()
                .HasOne(r => r.cliente)
                .WithMany()
                .HasForeignKey(r => r.id_Cliente);

            modelBuilder.Entity<Reserva>()
                .HasOne(r => r.livro)
                .WithMany()
                .HasForeignKey(r => r.id_Livro);
        }
    }
}
