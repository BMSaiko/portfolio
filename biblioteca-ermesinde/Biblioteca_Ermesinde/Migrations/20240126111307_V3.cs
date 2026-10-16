using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace BibliotecaErmesinde.Migrations
{
    /// <inheritdoc />
    public partial class V3 : Migration
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

            migrationBuilder.AlterColumn<int>(
                name: "LivroId",
                table: "reservas",
                type: "int",
                nullable: true,
                oldClrType: typeof(int),
                oldType: "int");

            migrationBuilder.AlterColumn<int>(
                name: "ClienteId",
                table: "reservas",
                type: "int",
                nullable: true,
                oldClrType: typeof(int),
                oldType: "int");

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

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_reservas_Clientes_ClienteId",
                table: "reservas");

            migrationBuilder.DropForeignKey(
                name: "FK_reservas_Livros_LivroId",
                table: "reservas");

            migrationBuilder.AlterColumn<int>(
                name: "LivroId",
                table: "reservas",
                type: "int",
                nullable: false,
                defaultValue: 0,
                oldClrType: typeof(int),
                oldType: "int",
                oldNullable: true);

            migrationBuilder.AlterColumn<int>(
                name: "ClienteId",
                table: "reservas",
                type: "int",
                nullable: false,
                defaultValue: 0,
                oldClrType: typeof(int),
                oldType: "int",
                oldNullable: true);

            migrationBuilder.AddForeignKey(
                name: "FK_reservas_Clientes_ClienteId",
                table: "reservas",
                column: "ClienteId",
                principalTable: "Clientes",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_reservas_Livros_LivroId",
                table: "reservas",
                column: "LivroId",
                principalTable: "Livros",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
