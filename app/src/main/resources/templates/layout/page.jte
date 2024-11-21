@import gg.jte.Content
@param Content content

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
        rel="stylesheet"
        integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3"
        crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
        crossorigin="anonymous"></script>
    <title>Анализатор страниц</title>
    </head>
    <body class="d-flex flex-column min-vh-100">
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
            <div class="container-fluid">
                <a class="navbar-brand" href="/">Анализатор страниц</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarNav">
                    <div class="navbar-nav">
                        <a class="nav-link" href="/">Главная</a>
                        <a class="nav-link" href="/urls">Сайты</a>
                    </div>
                </div>
            </div>
        </nav>
        <main class="flex-grow-1">
            <section>
                ${content}
            </section>
        </main>
        <footer class="footer mt-auto py-3 bg-light">
            <div class="container-xl">
                <div class="text-center">
                    <p>created by <a href="https://github.com/fireyorkUP" target="_blank">fireyorkUP</a></p>
                </div>
            </div>
        </footer>
    </body>
</html>