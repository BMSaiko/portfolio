@extends('app.template_app.template_index')
@section('main_content')
    <div class="container-fluid py-4">
      <div class="row mt-4">
        <div class="col-lg-9 col-12 mx-auto position-relative">
          <div class="card">
            <div class="card-body pt-2">
                <h6 class="mt-4">Insere o teu CV</h6>
              <div class="input-group input-group-dynamic mt-4">
                <label class="form-label">Ficheiro pdf</label>
                <form action="/file-upload" class="form-control dropzone" id="dropzone">
                  <div class="fallback">
                    <input name="file" id="file" type="file" accept=".pdf" />
                  </div>
              <div class="d-flex justify-content-end mt-4">
                <button type="button" name="button" class="btn bg-gradient-dark m-0 ms-2">Entregar</button>
              </div>
            </form>
            </div>
          </div>
        </div>
      </div>
    @endsection
