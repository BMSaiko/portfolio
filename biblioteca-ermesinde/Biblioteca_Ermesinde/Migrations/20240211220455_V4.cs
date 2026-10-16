using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace BibliotecaErmesinde.Migrations
{
    /// <inheritdoc />
    public partial class V4 : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_reservas_Clientes_ClienteId",
                table: "reservas");

            migrationBuilder.DropForeignKey(
                name: "FK_reservas_Livros_LivroId",
                table: "reservas");

            migrationBuilder.DropPrimaryKey(
                name: "PK_reservas",
                table: "reservas");

            migrationBuilder.RenameTable(
                name: "reservas",
                newName: "Reservas");

            migrationBuilder.RenameIndex(
                name: "IX_reservas_LivroId",
                table: "Reservas",
                newName: "IX_Reservas_LivroId");

            migrationBuilder.RenameIndex(
                name: "IX_reservas_ClienteId",
                table: "Reservas",
                newName: "IX_Reservas_ClienteId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_Reservas",
                table: "Reservas",
                column: "Id");

            migrationBuilder.CreateIndex(
                name: "IX_Reservas_id_Cliente",
                table: "Reservas",
                column: "id_Cliente");

            migrationBuilder.CreateIndex(
                name: "IX_Reservas_id_Livro",
                table: "Reservas",
                column: "id_Livro");

            migrationBuilder.AddForeignKey(
                name: "FK_Reservas_Clientes_ClienteId",
                table: "Reservas",
                column: "ClienteId",
                principalTable: "Clientes",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_Reservas_Clientes_id_Cliente",
                table: "Reservas",
                column: "id_Cliente",
                principalTable: "Clientes",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_Reservas_Livros_LivroId",
                table: "Reservas",
                column: "LivroId",
                principalTable: "Livros",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_Reservas_Livros_id_Livro",
                table: "Reservas",
                column: "id_Livro",
                principalTable: "Livros",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Reservas_Clientes_ClienteId",
                table: "Reservas");

            migrationBuilder.DropForeignKey(
                name: "FK_Reservas_Clientes_id_Cliente",
                table: "Reservas");

            migrationBuilder.DropForeignKey(
                name: "FK_Reservas_Livros_LivroId",
                table: "Reservas");

            migrationBuilder.DropForeignKey(
                name: "FK_Reservas_Livros_id_Livro",
                table: "Reservas");

            migrationBuilder.DropPrimaryKey(
                name: "PK_Reservas",
                table: "Reservas");

            migrationBuilder.DropIndex(
                name: "IX_Reservas_id_Cliente",
                table: "Reservas");

            migrationBuilder.DropIndex(
                name: "IX_Reservas_id_Livro",
                table: "Reservas");

            migrationBuilder.RenameTable(
                name: "Reservas",
                newName: "reservas");

            migrationBuilder.RenameIndex(
                name: "IX_Reservas_LivroId",
                table: "reservas",
                newName: "IX_reservas_LivroId");

            migrationBuilder.RenameIndex(
                name: "IX_Reservas_ClienteId",
                table: "reservas",
                newName: "IX_reservas_ClienteId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_reservas",
                table: "reservas",
                column: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_reservas_Clientes_ClienteId",
                table: "reservas",
                column: "ClienteId",
                principalTable: "Clientes",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_reservas_Livros_LivroId",
                table: "reservas",
                column: "LivroId",
                principalTable: "Livros",
                principalColumn: "Id");
        }
    }
}
