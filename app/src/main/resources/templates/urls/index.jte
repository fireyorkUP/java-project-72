@import hexlet.code.dto.urls.UrlsPage
@param UrlsPage page


@template.layout.page(
content = @`
    <section>
        @if(page.getFlash() != null)
            <div class="rounded-0 m-0 alert alert-dismissible fade show ${page.getFlashType()}" role="alert">
                <p class="m-0">${page.getFlash()}</p>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        @endif
        <div class="container-lg mt-5">
            <h1>Сайты</h1>
                <table class="table table-bordered table-hover mt-3">
                    <thead>
                        <tr>
                            <th class="col-1">ID</th>
                            <th>Имя</th>
                            <th class="col-2">Последняя проверка</th>
                            <th class="col-1">Код ответа</th>
                        </tr>
                    </thead>
                    <tbody>
                        @for(var url : page.getUrls())
                            <tr>
                                <td>
                                    ${url.getId()}
                                </td>
                                <td>
                                    <a href="/urls/${url.getId()}">${url.getName()}</a>
                                </td>
                                @if(page.getChecks().isEmpty())
                                    <td>
                                    </td>
                                    <td>
                                    </td>
                                @endif
                                @for(var check : page.getChecks())
                                    @if(check.getUrlId() == url.getId())
                                        <td>
                                            ${check.getCreatedAt().toString()}
                                        </td>
                                        <td>
                                            ${check.getStatusCode()}
                                        </td>
                                    @endif
                                @endfor
                            </tr>
                        @endfor
                    </tbody>
                </table>
        </div>
    </section>
`
)