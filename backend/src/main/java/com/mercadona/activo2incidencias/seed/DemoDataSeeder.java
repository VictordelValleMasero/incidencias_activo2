package com.mercadona.activo2incidencias.seed;

import com.mercadona.activo2incidencias.domain.entity.*;
import com.mercadona.activo2incidencias.domain.enums.EstadoActivo;
import com.mercadona.activo2incidencias.domain.enums.RolAdministrador;
import com.mercadona.activo2incidencias.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Crea, en el primer arranque, un administrador de demostracion y un
 * conjunto de datos de ejemplo (edificios, zonas, departamentos,
 * responsables y asignaciones zona/departamento/responsable) para que el
 * flujo "Comunicar incidencia" funcione de extremo a extremo sin pasos
 * manuales previos.
 *
 * La combinatoria de zonas x departamentos de esta demo (2 edificios, 18
 * zonas, 10 departamentos, ~14 responsables, ~90 asignaciones) es bastante
 * mayor que la del proyecto QRIncidencias original (que sembraba estos datos
 * via SQL en V2__seed_datos_demo.sql). Se ha optado por sembrarlos aqui, en
 * Java, en vez de en un SQL enorme y propenso a errores de tipeo: es el mismo
 * patron ApplicationRunner + "solo si no hay datos" que ya usaba el proyecto
 * hermano para el administrador de demo, simplemente ampliado en alcance.
 */
@Component
@RequiredArgsConstructor
public class DemoDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private static final String EMAIL_DEMO = "admin@activo2-demo.com";
    private static final String PASSWORD_DEMO = "Admin123!";

    private static final List<String> DEPARTAMENTOS_BUILDING_SPECIFIC =
            List.of("Seguridad", "Limpieza", "Mantenimiento", "Servicios generales");

    private static final List<String> DEPARTAMENTOS_COMPARTIDOS =
            List.of("Apoyo de informatica", "Climatizacion", "Electricidad", "Fontaneria", "Jardineria", "Otros");

    private final AdministradorRepository administradorRepository;
    private final EdificioRepository edificioRepository;
    private final ZonaRepository zonaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final ResponsableRepository responsableRepository;
    private final ZonaDepartamentoResponsableRepository asignacionRepository;
    private final PasswordEncoder passwordEncoder;

    private int contadorTelefono = 100;

    @Override
    public void run(ApplicationArguments args) {
        crearAdministradorDemoSiNoExiste();
        sembrarDatosDemoSiNoExisten();
    }

    @Transactional
    protected void crearAdministradorDemoSiNoExiste() {
        if (administradorRepository.count() > 0) {
            return;
        }

        Administrador admin = Administrador.builder()
                .nombre("Administrador Demo")
                .email(EMAIL_DEMO)
                .passwordHash(passwordEncoder.encode(PASSWORD_DEMO))
                .rol(RolAdministrador.ADMIN)
                .activo(true)
                .build();

        administradorRepository.save(admin);

        log.info("==========================================================");
        log.info(" Administrador de demo creado");
        log.info(" Email:      {}", EMAIL_DEMO);
        log.info(" Contrasena: {}", PASSWORD_DEMO);
        log.info(" Cambia estas credenciales antes de usar en produccion.");
        log.info("==========================================================");
    }

    @Transactional
    protected void sembrarDatosDemoSiNoExisten() {
        if (edificioRepository.count() > 0) {
            return;
        }

        Edificio jarrods = crearEdificio("Jarrods", "JARRODS", "Centro Jarrods", 1);
        Edificio campus = crearEdificio("Campus", "CAMPUS", "Campus corporativo", 2);

        Map<String, Departamento> departamentos = crearDepartamentos();
        Map<String, Responsable> responsables = crearResponsables(departamentos, jarrods, campus);
        asignarResponsablesPrincipales(departamentos, responsables);

        Map<String, Zona> zonasJarrods = crearZonasJarrods(jarrods);
        Map<String, Zona> zonasCampus = crearZonasCampus(campus);

        crearAsignaciones(jarrods, zonasJarrods, departamentos, responsables, mapaDepartamentosPorZonaJarrods());
        crearAsignaciones(campus, zonasCampus, departamentos, responsables, mapaDepartamentosPorZonaCampus());

        log.info("==========================================================");
        log.info(" Datos de demo creados: 2 edificios, {} zonas, {} departamentos, {} responsables",
                zonasJarrods.size() + zonasCampus.size(), departamentos.size(), responsables.size());
        log.info("==========================================================");
    }

    // ------------------------------------------------------------------
    // Edificios
    // ------------------------------------------------------------------

    private Edificio crearEdificio(String nombre, String codigo, String descripcion, int orden) {
        return edificioRepository.save(Edificio.builder()
                .nombre(nombre)
                .codigo(codigo)
                .descripcion(descripcion)
                .estado(EstadoActivo.ACTIVO)
                .orden(orden)
                .build());
    }

    // ------------------------------------------------------------------
    // Departamentos
    // ------------------------------------------------------------------

    private Map<String, Departamento> crearDepartamentos() {
        String[] nombres = {
                "Seguridad", "Limpieza", "Mantenimiento", "Apoyo de informatica", "Climatizacion",
                "Electricidad", "Fontaneria", "Jardineria", "Servicios generales", "Otros"
        };
        String[] descripciones = {
                "Incidencias de seguridad y control de accesos",
                "Limpieza e higiene de instalaciones",
                "Averias, reparaciones e instalaciones generales",
                "Soporte informatico y de sistemas",
                "Averias de climatizacion y ventilacion",
                "Incidencias electricas",
                "Averias de fontaneria y saneamiento",
                "Mantenimiento de zonas ajardinadas",
                "Servicios generales y logistica interna",
                "Cualquier otra incidencia no encuadrada en el resto de departamentos"
        };

        Map<String, Departamento> departamentos = new LinkedHashMap<>();
        for (int i = 0; i < nombres.length; i++) {
            Departamento departamento = departamentoRepository.save(Departamento.builder()
                    .nombre(nombres[i])
                    .descripcion(descripciones[i])
                    .estado(EstadoActivo.ACTIVO)
                    .build());
            departamentos.put(nombres[i], departamento);
        }
        return departamentos;
    }

    // ------------------------------------------------------------------
    // Responsables
    // ------------------------------------------------------------------

    private Map<String, Responsable> crearResponsables(Map<String, Departamento> departamentos, Edificio jarrods, Edificio campus) {
        Map<String, Responsable> responsables = new LinkedHashMap<>();

        String[][] nombresBuildingSpecific = {
                {"Antonio", "Ruiz Molina"}, {"Carmen", "Diaz Fernandez"},
                {"Javier", "Lopez Garcia"}, {"Sara", "Martinez Ortega"},
                {"Pablo", "Sanchez Romero"}, {"Elena", "Torres Navarro"},
                {"Miguel", "Gil Serrano"}, {"Isabel", "Vega Castro"}
        };
        int idx = 0;
        for (String depto : DEPARTAMENTOS_BUILDING_SPECIFIC) {
            Departamento departamento = departamentos.get(depto);

            Responsable respJarrods = crearResponsable(departamento,
                    nombresBuildingSpecific[idx % nombresBuildingSpecific.length][0],
                    nombresBuildingSpecific[idx % nombresBuildingSpecific.length][1],
                    "Responsable de " + depto + " (Jarrods)", "L-V 08:00-17:00");
            idx++;
            responsables.put(clave(depto, "JARRODS"), respJarrods);

            Responsable respCampus = crearResponsable(departamento,
                    nombresBuildingSpecific[idx % nombresBuildingSpecific.length][0],
                    nombresBuildingSpecific[idx % nombresBuildingSpecific.length][1],
                    "Responsable de " + depto + " (Campus)", "L-V 08:00-17:00");
            idx++;
            responsables.put(clave(depto, "CAMPUS"), respCampus);
        }

        String[][] nombresCompartidos = {
                {"David", "Moreno Ibanez"}, {"Lucia", "Alonso Pena"},
                {"Raul", "Jimenez Cano"}, {"Marta", "Herrera Suarez"},
                {"Sergio", "Ramos Ortiz"}, {"Cristina", "Nunez Vidal"}
        };
        int j = 0;
        for (String depto : DEPARTAMENTOS_COMPARTIDOS) {
            Departamento departamento = departamentos.get(depto);
            Responsable resp = crearResponsable(departamento,
                    nombresCompartidos[j % nombresCompartidos.length][0],
                    nombresCompartidos[j % nombresCompartidos.length][1],
                    "Responsable de " + depto, "L-V 09:00-18:00");
            j++;
            responsables.put(clave(depto, "SHARED"), resp);
        }

        return responsables;
    }

    private Responsable crearResponsable(Departamento departamento, String nombre, String apellidos, String cargo, String horario) {
        contadorTelefono++;
        String telefono = String.format("+34600000%03d", contadorTelefono);
        String email = (nombre + "." + apellidos.split(" ")[0]).toLowerCase()
                .replace("ñ", "n") + "@activo2-demo.com";
        return responsableRepository.save(Responsable.builder()
                .departamento(departamento)
                .nombre(nombre)
                .apellidos(apellidos)
                .cargo(cargo)
                .telefonoWhatsapp(telefono)
                .email(email)
                .horario(horario)
                .estado(EstadoActivo.ACTIVO)
                .build());
    }

    private void asignarResponsablesPrincipales(Map<String, Departamento> departamentos, Map<String, Responsable> responsables) {
        for (String depto : DEPARTAMENTOS_BUILDING_SPECIFIC) {
            Departamento departamento = departamentos.get(depto);
            departamento.setResponsablePrincipal(responsables.get(clave(depto, "JARRODS")));
            departamentoRepository.save(departamento);
        }
        for (String depto : DEPARTAMENTOS_COMPARTIDOS) {
            Departamento departamento = departamentos.get(depto);
            departamento.setResponsablePrincipal(responsables.get(clave(depto, "SHARED")));
            departamentoRepository.save(departamento);
        }
    }

    private String clave(String departamento, String ambito) {
        return departamento + "|" + ambito;
    }

    private Responsable responsableParaZona(String departamento, String codigoEdificio, Map<String, Responsable> responsables) {
        if (DEPARTAMENTOS_BUILDING_SPECIFIC.contains(departamento)) {
            return responsables.get(clave(departamento, codigoEdificio));
        }
        return responsables.get(clave(departamento, "SHARED"));
    }

    // ------------------------------------------------------------------
    // Zonas
    // ------------------------------------------------------------------

    private Map<String, Zona> crearZonasJarrods(Edificio jarrods) {
        String[] nombres = {"Recepcion", "Acceso principal", "Parking", "Aseos", "Oficinas", "Sala tecnica", "Comedor", "Almacen"};
        return crearZonas(jarrods, "JAR", nombres);
    }

    private Map<String, Zona> crearZonasCampus(Edificio campus) {
        String[] nombres = {"Garita principal", "Garita parking", "Aulario", "Banderas", "Parking", "Oficinas",
                "Aseos", "Comedor", "Salas tecnicas", "Accesos exteriores"};
        return crearZonas(campus, "CAM", nombres);
    }

    private Map<String, Zona> crearZonas(Edificio edificio, String prefijoCodigo, String[] nombres) {
        Map<String, Zona> zonas = new LinkedHashMap<>();
        int orden = 1;
        for (String nombre : nombres) {
            Zona zona = zonaRepository.save(Zona.builder()
                    .edificio(edificio)
                    .nombre(nombre)
                    .codigo(prefijoCodigo + "-" + String.format("%02d", orden))
                    .estado(EstadoActivo.ACTIVO)
                    .orden(orden)
                    .build());
            zonas.put(nombre, zona);
            orden++;
        }
        return zonas;
    }

    // ------------------------------------------------------------------
    // Departamentos disponibles por zona (usados para las asignaciones)
    // ------------------------------------------------------------------

    private Map<String, List<String>> mapaDepartamentosPorZonaJarrods() {
        Map<String, List<String>> mapa = new LinkedHashMap<>();
        mapa.put("Recepcion", List.of("Seguridad", "Mantenimiento", "Limpieza", "Apoyo de informatica", "Otros"));
        mapa.put("Acceso principal", List.of("Seguridad", "Mantenimiento", "Limpieza", "Otros"));
        mapa.put("Parking", List.of("Seguridad", "Mantenimiento", "Limpieza", "Electricidad", "Otros"));
        mapa.put("Aseos", List.of("Limpieza", "Mantenimiento", "Fontaneria", "Otros"));
        mapa.put("Oficinas", List.of("Limpieza", "Mantenimiento", "Apoyo de informatica", "Climatizacion", "Electricidad", "Servicios generales", "Otros"));
        mapa.put("Sala tecnica", List.of("Mantenimiento", "Electricidad", "Climatizacion", "Seguridad", "Otros"));
        mapa.put("Comedor", List.of("Limpieza", "Mantenimiento", "Servicios generales", "Fontaneria", "Otros"));
        mapa.put("Almacen", List.of("Seguridad", "Mantenimiento", "Limpieza", "Servicios generales", "Otros"));
        return mapa;
    }

    private Map<String, List<String>> mapaDepartamentosPorZonaCampus() {
        Map<String, List<String>> mapa = new LinkedHashMap<>();
        mapa.put("Garita principal", List.of("Seguridad", "Mantenimiento", "Otros"));
        mapa.put("Garita parking", List.of("Seguridad", "Mantenimiento", "Otros"));
        mapa.put("Aulario", List.of("Limpieza", "Mantenimiento", "Apoyo de informatica", "Climatizacion", "Electricidad", "Otros"));
        mapa.put("Banderas", List.of("Mantenimiento", "Jardineria", "Seguridad", "Otros"));
        mapa.put("Parking", List.of("Seguridad", "Mantenimiento", "Limpieza", "Electricidad", "Otros"));
        mapa.put("Oficinas", List.of("Limpieza", "Mantenimiento", "Apoyo de informatica", "Climatizacion", "Servicios generales", "Otros"));
        mapa.put("Aseos", List.of("Limpieza", "Mantenimiento", "Fontaneria", "Otros"));
        mapa.put("Comedor", List.of("Limpieza", "Mantenimiento", "Servicios generales", "Fontaneria", "Otros"));
        mapa.put("Salas tecnicas", List.of("Mantenimiento", "Electricidad", "Climatizacion", "Seguridad", "Apoyo de informatica", "Otros"));
        mapa.put("Accesos exteriores", List.of("Seguridad", "Mantenimiento", "Jardineria", "Limpieza", "Otros"));
        return mapa;
    }

    private void crearAsignaciones(Edificio edificio, Map<String, Zona> zonas, Map<String, Departamento> departamentos,
                                    Map<String, Responsable> responsables, Map<String, List<String>> departamentosPorZona) {
        for (Map.Entry<String, List<String>> entry : departamentosPorZona.entrySet()) {
            Zona zona = zonas.get(entry.getKey());
            for (String nombreDepartamento : entry.getValue()) {
                Departamento departamento = departamentos.get(nombreDepartamento);
                Responsable responsable = responsableParaZona(nombreDepartamento, edificio.getCodigo(), responsables);

                asignacionRepository.save(ZonaDepartamentoResponsable.builder()
                        .edificio(edificio)
                        .zona(zona)
                        .departamento(departamento)
                        .responsable(responsable)
                        .estado(EstadoActivo.ACTIVO)
                        .build());
            }
        }
    }
}
