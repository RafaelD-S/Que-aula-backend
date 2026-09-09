# Que Aula API - Guia de Integracao para Front-end

Este README documenta os endpoints disponiveis e os detalhes necessarios para conectar uma aplicacao front-end a esta API.

## 1. Visao geral

- Stack: Spring Boot 3 + Java 17 + JPA
- Base path da API: `/api`
- Formato: JSON
- Autenticacao: nenhuma (nao ha JWT/OAuth/session)

Entidades principais:
- Subject (disciplina)
- Section (turma, identificada por chave composta)
- Course (aula/horario vinculado a uma turma)

## 2. URL base por ambiente

A API usa profile Spring:
- `dev` (padrao): porta `8081`
- `prod`: porta `8080`

Configuracao observada:
- `src/main/resources/application.properties` define profile ativo em `dev` por padrao
- `src/main/resources/application-dev.properties` usa `server.port=8081`
- `src/main/resources/application-prod.properties` usa `server.port=8080`

Exemplos de base URL:
- Dev local: `http://localhost:8081/api`
- Prod local/container: `http://localhost:8080/api`

## 3. Como subir o backend

Com Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Com profile explicito:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

## 4. CORS e conectividade do front-end

Nao existe configuracao de CORS explicita no codigo.

Implicacoes:
- Se front e API rodarem em origens diferentes (ex.: `localhost:5173` e `localhost:8081`), o browser pode bloquear requisicoes.
- Para desenvolvimento local, o ideal e adicionar CORS no backend ou usar proxy no dev server do front-end.

## 5. Parametro `expand`

Alguns GETs aceitam query param `expand` para trazer relacoes.

Formato:
- `?expand=sections`
- `?expand=sections,courses`
- `?expand=courses`

Regras importantes:
- Case-insensitive (`SECTIONS`, `sections`, etc.)
- Aceita aliases singular/plural em alguns casos:
  - `section` <-> `sections`
  - `course` -> `courses`
  - `subjects` -> `subject`

## 6. Endpoints

## 6.1 Subjects

### GET `/subjects`
Retorna lista de disciplinas.

Query params:
- `expand` (opcional)
  - sem `expand`: lista de `SubjectResponseDTO`
  - `expand=sections`: lista de `SubjectFullDTO` com sections
  - `expand=sections,courses`: lista de `SubjectFullDTO` com sections e courses em cada section

Resposta sem expand (200):

```json
[
  {
    "code": "INF001",
    "name": "Algoritmos",
    "semester": 1
  }
]
```

Resposta com `expand=sections,courses` (200):

```json
[
  {
    "code": "INF001",
    "name": "Algoritmos",
    "semester": 1,
    "sections": [
      {
        "code": "A",
        "isStrike": false,
        "subjectCode": "INF001",
        "courses": [
          {
            "idCourse": 1,
            "sectionCode": "A",
            "subjectCode": "INF001",
            "teacher": "Maria",
            "classroom": "Lab 1",
            "weekday": 1,
            "periodStart": 0,
            "periodEnd": 1
          }
        ]
      }
    ]
  }
]
```

### GET `/subjects/{code}`
Busca disciplina por codigo.

Path params:
- `code` (string)

Query params:
- `expand` (opcional), mesmas regras do endpoint de listagem

Resposta:
- sem `expand`: `SubjectResponseDTO`
- com `expand=sections` ou `expand=sections,courses`: `SubjectFullDTO`

### POST `/subjects`
Cria disciplina.

Body (`SubjectDTO`):

```json
{
  "code": "INF001",
  "name": "Algoritmos",
  "semester": 1
}
```

Validacoes:
- `code`: obrigatorio, nao vazio
- `name`: obrigatorio, nao vazio
- `semester`: obrigatorio, inteiro entre 0 e 6

Resposta (200): `SubjectResponseDTO`

### PUT `/subjects/{code}`
Atualiza disciplina existente.

Body: mesmo formato do POST

Resposta (200): entidade `Subject` (nao DTO)

Observacao importante para front-end:
- O contrato de resposta do PUT e diferente do GET/POST (retorna entidade JPA, nao `SubjectResponseDTO`).

### DELETE `/subjects/{code}`
Remove disciplina.

Resposta: `200` com corpo vazio.

---

## 6.2 Sections

A identificacao de Section usa chave composta:
- `subjectCode` + `code`

### GET `/sections`
Lista turmas.

Query params:
- `expand` (opcional)
  - sem `expand`: `SectionResponseDTO[]`
  - `expand=courses`: `SectionFullDTO[]`

Resposta sem expand (200):

```json
[
  {
    "code": "A",
    "isStrike": false,
    "subjectCode": "INF001"
  }
]
```

Resposta com `expand=courses` (200):

```json
[
  {
    "code": "A",
    "isStrike": false,
    "subjectCode": "INF001",
    "courses": [
      {
        "idCourse": 1,
        "sectionCode": "A",
        "subjectCode": "INF001",
        "teacher": "Maria",
        "classroom": "Lab 1",
        "weekday": 1,
        "periodStart": 0,
        "periodEnd": 1
      }
    ]
  }
]
```

### GET `/sections/{subjectCode}/{code}`
Busca turma por chave composta.

Path params:
- `subjectCode` (string)
- `code` (string)

Query params:
- `expand` (opcional)
  - sem expand: `SectionResponseDTO`
  - `expand=courses`: `SectionFullDTO`

### POST `/sections`
Cria turma.

Body (`SectionDTO`):

```json
{
  "code": "A",
  "isStrike": false,
  "subjectCode": "INF001"
}
```

Validacoes:
- `code`: obrigatorio, nao vazio
- `isStrike`: obrigatorio (boolean)
- `subjectCode`: obrigatorio, nao vazio

Resposta (200): `SectionResponseDTO`

### PUT `/sections/{subjectCode}/{code}`
Atualiza turma.

Body: mesmo formato do POST

Resposta (200): entidade `Section` (nao DTO)

Observacao importante para front-end:
- Assim como em Subject, o PUT nao retorna o mesmo formato dos endpoints de leitura.

### DELETE `/sections/{subjectCode}/{code}`
Remove turma.

Resposta: `200` com corpo vazio.

---

## 6.3 Courses

### GET `/courses`
Lista cursos/aulas.

Query params:
- `expand` (opcional): `section` ou `subject`

Observacao:
- Mesmo com `expand`, o retorno continua `CourseResponseDTO[]` com campos planos (`sectionCode`, `subjectCode`).
- O `expand` impacta carregamento interno no backend, nao o shape final desse endpoint.

Resposta (200):

```json
[
  {
    "idCourse": 1,
    "sectionCode": "A",
    "subjectCode": "INF001",
    "teacher": "Maria",
    "classroom": "Lab 1",
    "weekday": 1,
    "periodStart": 0,
    "periodEnd": 1
  }
]
```

### GET `/courses/{id}`
Busca course por id.

Path params:
- `id` (number)

Query params:
- `expand` (opcional): `section` ou `subject`

Resposta: `CourseResponseDTO`

### POST `/courses`
Cria course.

Body (`CourseDTO`):

```json
{
  "sectionCode": "A",
  "subjectCode": "INF001",
  "teacher": "Maria",
  "classroom": "Lab 1",
  "weekday": 1,
  "periodStart": 0,
  "periodEnd": 1
}
```

Validacoes:
- `sectionCode`: obrigatorio, nao vazio
- `subjectCode`: obrigatorio, nao vazio
- `teacher`: obrigatorio, nao vazio
- `classroom`: obrigatorio, nao vazio
- `weekday`: obrigatorio, inteiro entre 0 e 6
- `periodStart`: obrigatorio, inteiro entre 0 e 5
- `periodEnd`: obrigatorio, inteiro entre 0 e 5

Resposta (200): entidade `Course` (nao DTO)

### PUT `/courses/{id}`
Atualiza course.

Path params:
- `id` (number)

Body: mesmo formato do POST

Resposta (200): entidade `Course` (nao DTO)

### DELETE `/courses/{id}`
Remove course.

Resposta: `200` com corpo vazio.

## 7. Resumo de contratos de resposta (importante para front)

A API tem contratos mistos entre DTO e entidade JPA.

- GETs retornam DTOs estaveis para leitura.
- POST de Subject/Section retorna DTO.
- POST/PUT de Course retorna entidade.
- PUT de Subject/Section retorna entidade.

Recomendacao para front-end:
- Sempre tipar respostas de escrita (POST/PUT) separadamente das respostas de leitura.
- Em fluxos criticos, apos criar/atualizar, fazer novo GET para normalizar dados na UI.

## 8. Erros HTTP e tratamento no front-end

Casos observados no codigo:
- Falha de validacao (`@Valid`): tende a retornar `400 Bad Request`
- `ResourceNotFoundException` e lancada para recursos nao encontrados

Observacao importante:
- Nao existe `@ControllerAdvice`/`@ExceptionHandler` customizado no projeto.
- Sem mapeamento explicito, `ResourceNotFoundException` pode responder como `500 Internal Server Error` em vez de `404`.

Formato de erro padrao Spring Boot (exemplo):

```json
{
  "timestamp": "2026-04-01T12:00:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/api/subjects/INF999"
}
```

Recomendacao para front-end:
- Tratar pelo menos `400`, `404`, `409`, `500` com mensagens amigaveis.
- Nao assumir shape fixo de erro ate existir handler global padronizado.

## 9. Tipagem sugerida no front-end (TypeScript)

```ts
export type SubjectResponse = {
  code: string;
  name: string;
  semester: number;
};

export type CourseResponse = {
  idCourse: number;
  sectionCode: string;
  subjectCode: string;
  teacher: string;
  classroom: string;
  weekday: number;
  periodStart: number;
  periodEnd: number;
};

export type SectionResponse = {
  code: string;
  isStrike: boolean;
  subjectCode: string;
};

export type SectionFull = SectionResponse & {
  courses: CourseResponse[];
};

export type SubjectFull = SubjectResponse & {
  sections: SectionFull[];
};
```

## 10. Exemplo de cliente HTTP para front-end

### Axios

```ts
import axios from "axios";

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8081/api",
  headers: {
    "Content-Type": "application/json"
  }
});

export async function listSubjects(expand?: string) {
  const { data } = await api.get("/subjects", { params: { expand } });
  return data;
}

export async function createCourse(payload: {
  sectionCode: string;
  subjectCode: string;
  teacher: string;
  classroom: string;
  weekday: number;
  periodStart: number;
  periodEnd: number;
}) {
  const { data } = await api.post("/courses", payload);
  return data;
}
```

### Fetch

```ts
const API_BASE = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8081/api";

export async function getSectionsWithCourses() {
  const res = await fetch(`${API_BASE}/sections?expand=courses`);

  if (!res.ok) {
    throw new Error(`Erro ao buscar sections: ${res.status}`);
  }

  return res.json();
}
```

## 11. Checklist rapido para conectar o front

1. Definir `VITE_API_BASE_URL` apontando para `/api`.
2. Conferir se backend esta no profile/porta esperados (`8081` dev, `8080` prod).
3. Validar CORS ou configurar proxy de desenvolvimento.
4. Implementar tratamento de erro por status HTTP.
5. Tipar separadamente respostas de GET e POST/PUT.
6. Usar `expand` apenas quando precisar reduzir round-trips para dados relacionais.

## 12. Referencias no codigo

- Controllers:
  - `src/main/java/com/ifba/que_aula/controller/SubjectController.java`
  - `src/main/java/com/ifba/que_aula/controller/SectionController.java`
  - `src/main/java/com/ifba/que_aula/controller/CourseController.java`
- DTOs:
  - `src/main/java/com/ifba/que_aula/dto/`
- Servicos e regras de `expand`:
  - `src/main/java/com/ifba/que_aula/service/`
  - `src/main/java/com/ifba/que_aula/utils/ExpandField.java`
- Configuracoes de ambiente:
  - `src/main/resources/application.properties`
  - `src/main/resources/application-dev.properties`
  - `src/main/resources/application-prod.properties`
